package plugin_metrics;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import data_representation.*;
/**
 * 
 * @author miriamhuijser
 * Class HellingerFunction provides methods that compute the hellinger distance
 * between two distributions or finds the closest cluster to a certain document.
 */
public class HellingerFunction extends Metric{
	boolean relativeFreq;

	/**
	 * Constructor
	 * @param relativeFreq - boolean that indicates whether the representation
	 * uses relative frequency/probability or the "normal" frequency of the 
	 * words/wordpairs.
	 */
	public HellingerFunction(boolean relativeFreq){
		this.relativeFreq = relativeFreq;
	}
	
	/**
	 * This method determines which method should be used to compute the hellinger
	 * distance between two documents, depending on whether the relative frequency
	 * (or probability) is used or the mere frequency of words. In that case
	 * the corpus sizes of the two documents will be used in the computation.
	 * @param q - first document
	 * @param corpusSizeQ - corpus size of first document
	 * @param r - second document
	 * @param corpusSizeR - corpus size of second document
	 * @return distance - resulting similarity score
	 */	
	public double computeDistance(Map<String, Double> q, int corpusSizeQ, 
			Map<String, Double> r, int corpusSizeR){
		double distance = 0;
		if(relativeFreq){
			distance = computeHellinger(q, r);
		}
		else distance = computeHellinger(q, corpusSizeQ, r, corpusSizeR);
		return distance;
	}

	/**
	 * This method determines the closest centroids for a document and returns
	 * a list of the corresponding indices in the clusters-list.
	 * @param doc - document for which the closest centroids need to be 
	 * determined
	 * @param clusters - list of clusters
	 * @return closestCentroids - list of indices corresponding to the closest
	 * centroids.
	 */
	public ArrayList<Integer> getClosestCentroids(Document doc, 
			ArrayList<Cluster> clusters){
		ArrayList<Integer> closestCentroids = new ArrayList<Integer>();
		double bestDistance = Double.POSITIVE_INFINITY;
		for( int c = 0; c < clusters.size(); c++){
			double distance = computeDistance(
								clusters.get(c).centroid.distribution,
								clusters.get(c).centroid.distributionSize,
								doc.words,
								doc.corpusSize
								);
			if( distance == bestDistance ){
				closestCentroids.add(c);
			}
			else if(distance < bestDistance){
				closestCentroids.clear();
				closestCentroids.add(c);
				bestDistance = distance;
			}
		}
		return closestCentroids;
	}

	/**
	 * This method computes the hellinger distance between two documents
	 * that are represented using the counts (frequency) of the words
	 * rather than their relative frequencies.
	 * @param q - first document
	 * @param corpusSizeQ - corpus size first document
	 * @param r - second document
	 * @param corpusSizeR - corpus size second document
	 * @return result - similarity score
	 */
	public static double computeHellinger( Map<String, Double> q, 
			int corpusSizeQ, Map<String, Double> r, int corpusSizeR ){
		double result = 0;
		ArrayList<String> vocab = new ArrayList<String>();
		for( Entry<String, Double> entry:q.entrySet() ){
			String word = entry.getKey();
			double q1 = entry.getValue() / (double) corpusSizeQ;
			double r1 = 0;
			if( r.containsKey(word) ){
				r1 = r.get(word) / (double) corpusSizeR;
			}
			double sq = Math.sqrt(q1) - Math.sqrt(r1);
			result = result + Math.pow(sq, 2);
			vocab.add(word);
		}
		for( Entry<String, Double> entry:r.entrySet() ){
			if( !vocab.contains(entry.getKey()) ){
				double r1 = entry.getValue() / (double) corpusSizeR;
				result = result + r1;
			}
		}
		return result;
	}

	/**
	 * This method computes the hellinger distance between two documents
	 * that are represented using the relative frequencies of the words.
	 * @param q - first document
	 * @param r - second document
	 * @return result - similarity score
	 */
	public static double computeHellinger( Map<String, Double> q, 
			Map<String, Double> r ){
		double result = 0;
		ArrayList<String> vocab = new ArrayList<String>();
		for( Entry<String, Double> entry:q.entrySet() ){
			String word = entry.getKey();
			double q1 = entry.getValue();
			double r1 = 0;
			if( r.containsKey(word) ){
				r1 = r.get(word);
			}
			double sq = Math.sqrt(q1) - Math.sqrt(r1);
			result = result + Math.pow(sq, 2);
			vocab.add(word);
		}
		for( Entry<String, Double> entry:r.entrySet() ){
			if( !vocab.contains(entry.getKey()) ){
				double r1 = entry.getValue();
				result = result + r1;
			}
		}
		return result;
	}	
}