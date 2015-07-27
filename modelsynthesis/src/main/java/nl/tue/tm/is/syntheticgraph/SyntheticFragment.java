package nl.tue.tm.is.syntheticgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.didion.jwnl.JWNLException;

public class SyntheticFragment {
	
	Hashtable<String,List<String>> KeyWord2Label;
	//Hashtable<String,List<String>> Label2Fragment;
	Set<String> LabelSet;
	List<String> LabelList;
	
	SyntheticFragment(){
		KeyWord2Label = new Hashtable<String,List<String>>();
		//Label2Fragment = new Hashtable<String,List<String>>();		
		LabelSet = new HashSet<String>();
		LabelList = new ArrayList<String>();
	}
	
	SyntheticFragment(SyntheticFragment sf){
		KeyWord2Label = new Hashtable<String,List<String>>();
		KeyWord2Label = (Hashtable<String, List<String>>) sf.KeyWord2Label.clone();
		//Label2Fragment = new Hashtable<String,List<String>>();		
		LabelSet = new HashSet<String>(sf.LabelSet);
		LabelList = new ArrayList<String>(sf.LabelList);
	}
	
	void generateFragments(GraphCollectionProperty gcp) throws IOException, JWNLException{
		//GraphCollectionProperity gcp = new GraphCollectionProperity();
		//gcp.getProperties();
		//System.out.println(gcp.KeyWordList);
		//System.out.println(gcp.WordsInCurrentLabel);
		Random r = new Random();
		generateLabels(gcp,r);
		System.out.println("gcp.LabelList:"+gcp.LabelList.size());
		System.out.println("gcp.LabelSet:"+gcp.LabelSet.size());
		System.out.println("sf.LabelList:"+this.LabelList.size());
		System.out.println("sf.LabelSet:"+this.LabelSet.size());
	}
	
	
	
	private void generateLabels(GraphCollectionProperty gcp, Random r){
		String keyword;
		int size;
		List<String> words;
		String label;
		List<String> tempStringList;
		ArrayList<Integer> tempIntList;
		//while(gcp.KeyWordList.size()>0){
		while(1.2*gcp.LabelSet.size()>this.LabelSet.size()){

		//for(int i=0; i<gcp.LabelList.size(); i++){
			//tempStringList = gcp.KeyWordList;
			
			//for(String a: gcp.KeyWordList){
				//tempStringList.add(a);
			//}
			keyword = gcp.KeyWordList.get(r.nextInt(gcp.KeyWordList.size()));
			
			tempIntList = new ArrayList<Integer>();
			for(Integer s:gcp.WordNumInCurrentLabel4KeyWord.get(keyword))
				tempIntList.add(s);
			//System.out.println(r.nextInt(tempStringList.size())+"IntSize:"+tempIntList.size()+",Str:"+tempIntList);
			if(tempIntList.size()<=0)
				continue;
			else{
				size = tempIntList.get(r.nextInt(tempIntList.size()));
				//tempIntList.r
				
				
				words = new ArrayList<String>();
				
				tempIntList = new ArrayList<Integer>();
				tempStringList= new ArrayList<String>();
				for(String s:gcp.WordsInCurrentLabel.get(keyword))
					tempStringList.add(s);
				//tempStringList = gcp.WordsInCurrentLabel.get(keyword);
				//System.out.println("StrSize:"+tempStringList.size()+",Str:"+tempStringList);
				if(tempStringList.size()<size){
					continue;
					//words = new ArrayList<String>(tempStringList);
					//tempStringList.clear();
				}
				else
					for(int j=0;j<size-1||tempStringList.isEmpty();j++){	
						words.add(tempStringList.remove(r.nextInt(tempStringList.size())));
					}
				words.add(keyword);
				label = constructLabel(words);
			}
			
			LabelSet.add(label);
			LabelList.add(label);
			
			for(String keyword1:label.split(" "))
			if(KeyWord2Label.containsKey(keyword1)){
				KeyWord2Label.get(keyword1).add(label);
			}
			else{
				tempStringList =new ArrayList<String>();
				tempStringList.add(label);
				KeyWord2Label.put(keyword1, tempStringList);
			}
				
		}
		
	}
	

	private String constructLabel(List<String> words) {
		Collections.sort(words);
		String label = "";
		for(String word:words)
			label += word+" "; 
		return label.trim().toLowerCase();
		
	}
	
	
	public static void main(String[] args) throws IOException, JWNLException{
		
	}
	
}
