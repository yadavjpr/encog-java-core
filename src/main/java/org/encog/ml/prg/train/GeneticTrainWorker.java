package org.encog.ml.prg.train;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.crossover.PrgCrossover;
import org.encog.ml.prg.train.mutate.PrgMutate;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.neural.networks.training.CalculateScore;

public class GeneticTrainWorker extends Thread {
	private final PrgGenetic owner;
	private AtomicBoolean done = new AtomicBoolean();
	private EncogProgram[] tempProgram;
	private Random rnd;
	
	public GeneticTrainWorker(PrgGenetic theOwner) {
		this.owner = theOwner;
		this.rnd = this.owner.getRandomNumberFactory().factor();
		
		this.tempProgram = new EncogProgram[1];
		for(int i=0;i<1;i++) {
			this.tempProgram[i] = this.owner.getPopulation().createProgram();
		}
		
	}
	
	public void run() {
		
		try {
		PrgPopulation population = this.owner.getPopulation();
		EncogProgram[] members = this.owner.getPopulation().getMembers();
		PrgSelection selection = this.owner.getSelection();
		PrgCrossover crossover = this.owner.getCrossover();
		PrgMutate mutation = this.owner.getMutation();
		CalculateScore scoreFunction = this.owner.getScoreFunction();
		this.done.set(false);
		
		for(;;) {
			EncogProgram newPrg = null;
			EncogProgram parent1 = members[selection.performSelection()];
			
			if( this.rnd.nextDouble()<0.9 ) {
				
				EncogProgram parent2 = members[selection.performSelection()];
				crossover.crossover(this.rnd, parent1, parent2, this.tempProgram,0,1);
			} else {
				newPrg = mutation.mutate(this.rnd, parent1);
			}
			
			double score = scoreFunction.calculateScore(newPrg);
			if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
				population.rewrite(newPrg);
				newPrg.setScore(score);
				this.owner.addGenome(this.tempProgram,0,1);
				
				if( this.done.get() ) {
					break;
				}
			}
		}} catch(Throwable t) {
			this.owner.reportError(t);
		}
	}
	
	public void requestTerminate() {
		this.done.set(true);
	}
}