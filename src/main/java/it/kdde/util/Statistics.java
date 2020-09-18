package it.kdde.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The Statistics class. This class collects some statistical data about the
 * sequence tree building process.
 * 
 */
public class Statistics {

	public static final String header = "algorithm,dataset,minSupport,absSupport,itemsetTime,sequenceTime,totalTime,startMemory,endMemory,totalMemory,numSequences";

	public static final String headerClosed = "algorithm,dataset,minSupport,absSupport,itemsetTime,sequenceTime,totalTime,startMemory," +
			"endMemory,totalMemory,numClosedSequences,numPrunedSequences,numGenerated";

	private long startTimeItemset;
	private long endTimeItemset;
	
	private long startTimeSequence;
	private long endTimeSequence;

	private MemoryLogger memoryLogger = MemoryLogger.getInstance();
	private double startMemory;
	private double endMemory;

	private int numFrequentSequences;


	private int numClosedFrequentSequences;
	private int numFrequentSequenceGenerated;
	private int numSequencesPruned;

	public void startTimeItemset() {
		startTimeItemset = System.currentTimeMillis();
	}

	public void endTimeItemset() {
		endTimeItemset = System.currentTimeMillis();
	}
	
	public void startTimeSequence(){
		startTimeSequence = System.currentTimeMillis();
	}
	
	public void endTimeSequence(){
		endTimeSequence= System.currentTimeMillis();
	}

	public long totalTime() {
		return (endTimeItemset - startTimeItemset) + ( endTimeSequence - startTimeSequence);
	}
	
	public long itemsetTime() {
		return endTimeItemset - startTimeItemset;
	}
	
	public long sequenceTime(){
		return endTimeSequence - startTimeSequence;
	}

	public void startMemory(){
		memoryLogger.checkMemory();
		startMemory = memoryLogger.getMaxMemory();
	}

	public void endMemory(){
		memoryLogger.checkMemory();
		endMemory = memoryLogger.getMaxMemory();
	}

	public int getNumFrequentSequences() {
		return numFrequentSequences;
	}

	public void setNumFrequentSequences(int numFrequentSequences) {
		this.numFrequentSequences = numFrequentSequences;
	}

	public int getNumClosedFrequentSequences() {
		return numClosedFrequentSequences;
	}

	public void setNumClosedFrequentSequences(int numClosedFrequentSequences) {
		this.numClosedFrequentSequences = numClosedFrequentSequences;
	}

	public int getNumFrequentSequenceGenerated() {
		return numFrequentSequenceGenerated;
	}

	public void setNumFrequentSequenceGenerated(int numFrequentSequenceGenerated) {
		this.numFrequentSequenceGenerated = numFrequentSequenceGenerated;
	}

	public void setNumSequencesPruned(int numSequencesPruned) {
		this.numSequencesPruned = numSequencesPruned;
	}

	public int getNumSequencesPruned() {
		return numSequencesPruned;
	}

	/**
	 * print stats for FAST
	 * @param datasetName
	 * @param minSupp
	 * @param absMinSup
	 * @param statisticsFile
	 * @throws java.io.IOException
	 */
	public void printFrequentSequencesStat(String algorithmName, String datasetName, float minSupp, int absMinSup, String statisticsFile) throws IOException {

		Path statsFile = Paths.get(statisticsFile);
		BufferedWriter out;

		if (Files.exists(statsFile)){
			//open in append
			out = Files.newBufferedWriter(statsFile, StandardOpenOption.APPEND);
		}else {
			//create new
			out = Files.newBufferedWriter(statsFile, StandardOpenOption.CREATE_NEW);
			out.write(header + "\n");
		}
		out.write(algorithmName + "," + datasetName + "," + minSupp + "," + absMinSup + "," + Utils.toSeconds(itemsetTime())
				+ "," + Utils.toSeconds(sequenceTime())  + "," + Utils.toSeconds(totalTime())  + "," +
				startMemory + "," + endMemory + "," + (endMemory - startMemory)
				+ "," + numFrequentSequences + "\n");
		out.flush();
		out.close();
	}

	/**
	 * CloFAST
	 * @param datasetName
	 * @param minSupp
	 * @param absMinSup
	 * @param statisticsFile
	 * @throws java.io.IOException
	 */
	public void printClosedSequencesStat(String algorithmName, String datasetName, float minSupp, int absMinSup, String statisticsFile) throws IOException {

		Path statsFile = Paths.get(statisticsFile);
		BufferedWriter out;

		if (Files.exists(statsFile)){
			//open in append
			out = Files.newBufferedWriter(statsFile, StandardOpenOption.APPEND);
		}else {
			//create new
			out = Files.newBufferedWriter(statsFile, StandardOpenOption.CREATE_NEW);
			out.write(headerClosed + "\n");
		}

		out.write(algorithmName + "," + datasetName + "," + minSupp + "," + absMinSup + "," + Utils.toSeconds(itemsetTime())
				+ "," + Utils.toSeconds(sequenceTime())  + "," + Utils.toSeconds(totalTime())  + "," +
				startMemory + "," + endMemory + "," + (endMemory - startMemory)
				+ "," + numClosedFrequentSequences + "," + numSequencesPruned + "," + numFrequentSequenceGenerated + "\n");
		out.flush();
		out.close();
	}
}
