package org.encog.app.generate;

import java.io.File;

import org.encog.app.generate.generators.LanguageSpecificGenerator;
import org.encog.app.generate.generators.java.GenerateEncogJava;
import org.encog.app.generate.program.EncogProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.ml.MLMethod;

public class EncogCodeGeneration {
	
	private final TargetLanguage targetLanguage; 
	private final File targetFile;
	private boolean embedData;
	private MLMethod method;
	private LanguageSpecificGenerator generator;
	private final EncogProgram program = new EncogProgram();	
	
	public EncogCodeGeneration(TargetLanguage theTargetLanguage, File theTargetFile) {
		this.targetLanguage = theTargetLanguage;
		this.targetFile = theTargetFile;
		
		switch( theTargetLanguage ) {
			case Java:
				this.generator = new GenerateEncogJava();
				break;
		}
	}
		
	/**
	 * @return the targetLanguage
	 */
	public TargetLanguage getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * @return the targetFile
	 */
	public File getTargetFile() {
		return targetFile;
	}


	public void generate() {
		this.program.addComment("Hello World");
		EncogProgramNode mainClass = this.program.createClass("EncogExample");
		EncogProgramNode mainFunction = mainClass.createMainFunction();
		
		this.generator.generate(this.program);
		this.generator.writeContents(this.targetFile);
	}

	public boolean isEmbedData() {
		return embedData;
	}



	public void setEmbedData(boolean embedData) {
		this.embedData = embedData;
	}



	public MLMethod getMethod() {
		return method;
	}



	public void setMethod(MLMethod method) {
		this.method = method;
	}
	
	
	
}
