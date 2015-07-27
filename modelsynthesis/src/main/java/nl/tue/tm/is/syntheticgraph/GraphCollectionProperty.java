package nl.tue.tm.is.syntheticgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Set;

import net.didion.jwnl.JWNLException;
import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.graph.TransitiveGraph;




public class GraphCollectionProperty {
	
	//properities of labels
	List<Integer> NumLabelKeyWords; 
	List<Integer> NumLabelStopWords;
	List<String> KeyWordList; 
	Set<String> KeyWordSet;
	List<String> StopWordList; 
	Set<String> StopWordSet;
	List<String> GatewayList; 
	Set<String> GatewaySet;
	List<String> LabelList;
	Set<String> LabelSet;
	List<String> BlankLabelList;
	
	
	
	//properties of the label
	Hashtable<String, List<String>> WordsInCurrentLabel;
	Hashtable<String, List<String>> KeyWordsInPreLabel;
	Hashtable<String, List<String>> KeyWordsInPostLabel;
	
	Hashtable<String, List<Integer>> WordNumInCurrentLabel4KeyWord;
	//Hashtable<String, List<Integer>> StopWordNumInCurrentLabel4KeyWord;
	
	
	//properties of node (event and function) types
	Set<String> IntermediateNodeTypeSet;
	List<String> IntermediateNodeTypeList;
	Hashtable<String, List<String>> NodeTypeInPreNodes;
	Hashtable<String, List<String>> NodeTypeInPostNodes;
	
	Set<String> StartNodeTypeSet;
	List<String> StartNodeTypeList;
	Set<String> EndNodeTypeSet;
	List<String> EndNodeTypeList;
	
	//properties of gateway types
	Set<String> GatewayTypeSet;
	List<String> GatewayTypeList;
	//Hashtable<String, List<String>> GatewayTypeInPreNodes;
	//Hashtable<String, List<String>> GatewayTypeInPostNodes;
	
	//properties of the structure (feature)
	Hashtable<String, List<Integer>> InputEdges4KeyWord;
	Hashtable<String, List<Integer>> OutputEdges4KeyWord;
	
	Hashtable<String, List<Integer>> LoopEdges4KeyWord;//loop -1 (basic) -2 (transitive)
	
	Hashtable<String, List<Integer>> OutputEdgesofPreviousNode4KeyWord;
	Hashtable<String, List<Integer>> InputEdgesofNextNode4KeyWord;
	
	/*List<Integer> InputEdges;
	List<Integer> OutputEdges;
	List<Integer> OutputEdgesofPreviousNode;
	List<Integer> InputEdgesofNextNode;
	List<Integer> LoopEdges;
	*/
	
	Hashtable<Integer,List<Pattern4MultiNodes>> SplitPatterns;
	Hashtable<Integer,List<Pattern4MultiNodes>> JoinPatterns;
	Hashtable<Integer,List<Pattern4MultiNodes>> nonterminativeSplitPatterns;
	Hashtable<Integer,List<Pattern4MultiNodes>> nonterminativeJoinPatterns;
	
	List<Integer> SeqLength;
	
	//properities of features
	double collection_graphNum;
	List<Integer> NumGraphNodes0;
	List<Integer> NumGraphSeqs1;
	double[] NumGraphSeqs2;
	double[] NumGraphSplit2;
	double[] NumGraphSplit3;
	double[] NumGraphJoin2;
	double[] NumGraphJoin3;
	
	int NumGraphwithLoop;
	
	GraphCollectionProperty() throws IOException{
		readStopWords();
		NumLabelKeyWords = new ArrayList<Integer>(); 
		NumLabelStopWords = new ArrayList<Integer>();
		KeyWordList = new ArrayList<String>(); 
		KeyWordSet = new HashSet<String>();
		StopWordList = new ArrayList<String>(); 
		StopWordSet = new HashSet<String>();
		GatewayList = new ArrayList<String>(); 
		GatewaySet = new HashSet<String>();
		LabelList = new ArrayList<String>();
		LabelSet = new HashSet<String>();
		BlankLabelList = new ArrayList<String>();
		//properties of node type
		
		IntermediateNodeTypeSet = new HashSet<String>();
		IntermediateNodeTypeList = new ArrayList<String>();
		NodeTypeInPreNodes = new Hashtable<String, List<String>>();
		NodeTypeInPostNodes = new Hashtable<String, List<String>>();
		
		StartNodeTypeSet = new HashSet<String>();
		StartNodeTypeList = new ArrayList<String>();
		EndNodeTypeSet = new HashSet<String>();
		EndNodeTypeList = new ArrayList<String>();
		
		//properties of gateway types
		GatewayTypeSet = new HashSet<String>();
		GatewayTypeList = new ArrayList<String>();
		
		//properties of the label
		WordsInCurrentLabel = new Hashtable<String, List<String>>();
		KeyWordsInPreLabel = new Hashtable<String, List<String>>();
		KeyWordsInPostLabel = new Hashtable<String, List<String>>();
		
		WordNumInCurrentLabel4KeyWord = new Hashtable<String, List<Integer>>();
		//properties of the structure (feature)
		InputEdges4KeyWord = new Hashtable<String, List<Integer>>();
		OutputEdges4KeyWord = new Hashtable<String, List<Integer>>();
		
		LoopEdges4KeyWord = new Hashtable<String, List<Integer>>() ;//loop -1 (basic) -2 (transitive)
		
		OutputEdgesofPreviousNode4KeyWord = new Hashtable<String, List<Integer>>();
		InputEdgesofNextNode4KeyWord = new Hashtable<String, List<Integer>>();
		
		SplitPatterns = new Hashtable<Integer,List<Pattern4MultiNodes>>() ;
		JoinPatterns = new Hashtable<Integer,List<Pattern4MultiNodes>>() ;
		
		nonterminativeSplitPatterns = new Hashtable<Integer,List<Pattern4MultiNodes>>() ;
		nonterminativeJoinPatterns = new Hashtable<Integer,List<Pattern4MultiNodes>>() ;
		
		SeqLength = new ArrayList<Integer>();
		
		NumGraphNodes0 = new ArrayList<Integer>(); 
		NumGraphSeqs1 = new ArrayList<Integer>();
		
		NumGraphwithLoop = 0;
	}
	
	GraphCollectionProperty(GraphCollectionProperty gcp) throws IOException{
		
		//GraphCollectionProperity();
		NumLabelKeyWords = new ArrayList<Integer>(gcp.NumLabelKeyWords); 
		NumLabelStopWords = new ArrayList<Integer>(gcp.NumLabelStopWords);
		KeyWordList = new ArrayList<String>(gcp.KeyWordList); 
		KeyWordSet = new HashSet<String>(gcp.KeyWordSet);
		StopWordList = new ArrayList<String>(gcp.StopWordList); 
		StopWordSet = new HashSet<String>(gcp.StopWordSet);
		GatewayList = new ArrayList<String>(gcp.GatewayList); 
		GatewaySet = new HashSet<String>(gcp.GatewaySet);
		LabelList = new ArrayList<String>(gcp.LabelList);
		LabelSet = new HashSet<String>(gcp.LabelSet);
		BlankLabelList = new ArrayList<String>(gcp.BlankLabelList);
		
		
//properties of node type
		
		IntermediateNodeTypeSet = new HashSet<String>(gcp.IntermediateNodeTypeSet);
		IntermediateNodeTypeList = new ArrayList<String>(gcp.IntermediateNodeTypeList);
		NodeTypeInPreNodes = new Hashtable<String, List<String>>(gcp.NodeTypeInPreNodes);
		NodeTypeInPostNodes = new Hashtable<String, List<String>>(gcp.NodeTypeInPostNodes);
		
		StartNodeTypeSet = new HashSet<String>(gcp.StartNodeTypeSet);
		StartNodeTypeList = new ArrayList<String>(gcp.StartNodeTypeList);
		EndNodeTypeSet = new HashSet<String>(gcp.EndNodeTypeSet);
		EndNodeTypeList = new ArrayList<String>(gcp.EndNodeTypeList);
		
		//properties of gateway types
		GatewayTypeSet = new HashSet<String>(gcp.GatewayTypeSet);
		GatewayTypeList = new ArrayList<String>(gcp.GatewayTypeList);
		
		//properties of the label
		
		WordsInCurrentLabel = (Hashtable<String, List<String>>) gcp.WordsInCurrentLabel.clone();
		KeyWordsInPreLabel = (Hashtable<String, List<String>>)(gcp.KeyWordsInPreLabel.clone());
		KeyWordsInPostLabel = ( Hashtable<String, List<String>>)(gcp.KeyWordsInPostLabel.clone());
		
		WordNumInCurrentLabel4KeyWord = (Hashtable<String, List<Integer>>)(gcp.WordNumInCurrentLabel4KeyWord.clone());
		
		//properties of the structure (feature)
		InputEdges4KeyWord = (Hashtable<String, List<Integer>>)(gcp.InputEdges4KeyWord.clone());
		OutputEdges4KeyWord = (Hashtable<String, List<Integer>>)(gcp.OutputEdges4KeyWord.clone());
		
		LoopEdges4KeyWord = (Hashtable<String, List<Integer>>)(gcp.LoopEdges4KeyWord.clone()) ;//loop -1 (basic) -2 (transitive)
		
		OutputEdgesofPreviousNode4KeyWord = (Hashtable<String, List<Integer>>)(gcp.OutputEdgesofPreviousNode4KeyWord.clone());
		InputEdgesofNextNode4KeyWord = (Hashtable<String, List<Integer>>)(gcp.InputEdgesofNextNode4KeyWord.clone());
		
		SplitPatterns = (Hashtable<Integer,List<Pattern4MultiNodes>>)(gcp.SplitPatterns.clone()) ;
		JoinPatterns = (Hashtable<Integer, List<Pattern4MultiNodes>>) gcp.JoinPatterns.clone();
		//new Hashtable<Integer,List<Pattern4MultiNodes>>(gcp.JoinPatterns.clone()) ;
		
		
		nonterminativeSplitPatterns = (Hashtable<Integer,List<Pattern4MultiNodes>>)(gcp.nonterminativeSplitPatterns.clone()) ;
		nonterminativeJoinPatterns = (Hashtable<Integer,List<Pattern4MultiNodes>>)(gcp.nonterminativeJoinPatterns.clone()) ;
		
		SeqLength = new ArrayList<Integer>(gcp.SeqLength);
		
		NumGraphNodes0 = new ArrayList<Integer>(gcp.NumGraphNodes0); 
		NumGraphSeqs1 = new ArrayList<Integer>(gcp.NumGraphSeqs1);
		
		NumGraphwithLoop = gcp.NumGraphwithLoop;
		
	}
	
	void print(){
		//System.out.println(gcp.GatewaySet);
		
		System.out.println("Key Word Set:"+this.KeyWordSet.size());
		System.out.println("Key Word List:"+this.KeyWordList.size()+"---avg:"+this.KeyWordList.size()/collection_graphNum);
		double Num = 0;
		for(Integer num:this.NumLabelKeyWords){
			Num+=num;
		}
		System.out.println("Average Word Num In One Label:"+Num/this.NumLabelKeyWords.size());
		
		System.out.println("Label Set:"+this.LabelSet.size());
		System.out.println("Label List:"+this.LabelList.size()+"---avg:"+this.LabelList.size()/collection_graphNum);
		
		//System.out.println("KeyWordsInPreLabel"+gcp.KeyWordsInPreLabel);
		//System.out.println("KeyWordsInPostLabel"+gcp.KeyWordsInPostLabel);
		//System.out.println("WordNumInCurrentLabel4KeyWord:"+gcp.WordNumInCurrentLabel4KeyWord);
		//System.out.println("InputEdges4KeyWord:"+gcp.InputEdges4KeyWord);
		//System.out.println("OutputEdges4KeyWord:"+gcp.OutputEdges4KeyWord);
		//System.out.println("LoopEdges4KeyWord:"+gcp.LoopEdges4KeyWord);
		//System.out.println("OutputEdgesofPreviousNode4KeyWord:"+gcp.OutputEdgesofPreviousNode4KeyWord);
		//System.out.println("InputEdgesofNextNode4KeyWord:"+gcp.InputEdgesofNextNode4KeyWord);
		Num=0;
		for(Integer size: this.SplitPatterns.keySet()){
			int num = this.SplitPatterns.get(size).size();
			System.out.println("split size,num of split:"+size+","+num+";"+"---avg:"+num/collection_graphNum);
			Num+=num;
		}
		
		System.out.println("SplitPatterns:"+Num+"---avg:"+Num/collection_graphNum);
		
		Num=0;
		for(Integer size: this.JoinPatterns.keySet()){
			int num = this.JoinPatterns.get(size).size();
			System.out.println("join size,num of join:"+size+","+num+";"+"---avg:"+num/collection_graphNum);
			Num+=num;
		}
		
		System.out.println("JoinPatterns:"+Num+"---avg:"+Num/collection_graphNum);
		
		
		System.out.println("graph with loop:"+this.NumGraphwithLoop+"---avg:"+this.NumGraphwithLoop/collection_graphNum);
		
		System.out.println("sequences:"+this.SeqLength.size()+"---avg:"+this.SeqLength.size()/collection_graphNum);
		
		System.out.println("NodeTypeSet:"+IntermediateNodeTypeSet.size());
		System.out.println("NodeTypeList:"+IntermediateNodeTypeList.size()+"---avg:"+IntermediateNodeTypeList.size()/collection_graphNum);
		System.out.println("StartNodeTypeSet:"+StartNodeTypeSet);
		System.out.println("StartNodeTypeList:"+StartNodeTypeList.size()+"---avg:"+StartNodeTypeList.size()/collection_graphNum);
		System.out.println("EndNodeTypeSet:"+EndNodeTypeSet);
		System.out.println("EndNodeTypeList:"+IntermediateNodeTypeList.size()+"---avg:"+EndNodeTypeList.size()/collection_graphNum);
		int numNT;
		Map<String,Integer> NT = new HashMap<String,Integer>();
		for(String nt: IntermediateNodeTypeList){
			if(!NT.containsKey(nt))
				NT.put(nt, 1);
			else{
				numNT = NT.get(nt);
				NT.put(nt, numNT+1);
			}
				
		}
		for(String nt: StartNodeTypeList){
			if(!NT.containsKey(nt))
				NT.put(nt, 1);
			else{
				numNT = NT.get(nt);
				NT.put(nt, numNT+1);
			}
				
		}
		for(String nt: EndNodeTypeList){
			if(!NT.containsKey(nt))
				NT.put(nt, 1);
			else{
				numNT = NT.get(nt);
				NT.put(nt, numNT+1);
			}
				
		}
		System.out.println("NodeTypeMapping:");
		//System.out.println(NT);
		for(String t:NT.keySet()){
			System.out.println(t+"; num:"+NT.get(t)+"; avgnum:"+NT.get(t)/collection_graphNum);
		}
		
		System.out.println("GatewayTypeSet:"+GatewayTypeSet.size());
		System.out.println("GatewayTypeList:"+GatewayTypeList.size()+"---avg:"+GatewayTypeList.size()/collection_graphNum);
		numNT = 0;
		NT = new HashMap<String,Integer>();
		for(String nt: GatewayTypeList){
			if(!NT.containsKey(nt))
				NT.put(nt, 1);
			else{
				numNT = NT.get(nt);
				NT.put(nt, numNT+1);
			}
				
		}
		System.out.println("GatewayTypeMapping:");
		//System.out.println(NT);
		for(String t:NT.keySet()){
			System.out.println(t+"; num:"+NT.get(t)+"; avgnum:"+NT.get(t)/collection_graphNum);
		}
		
		/*//properties of node (event and function) types
		Set<String> NodeTypeSet;
		List<String> NodeTypeList;
		Hashtable<String, List<String>> NodeTypeInPreNodes;
		Hashtable<String, List<String>> NodeTypeInPostNodes;
		
		//properties of gateway types
		Set<String> GatewayTypeSet;
		List<String> GatewayTypeList;*/
		//return "";
	}
	
	static HashSet<String> stopwords = new HashSet<String>();
	
	private static void readStopWords() throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new FileReader("englishST.txt"));
		String stopword = bufferedReader.readLine();
		while(stopword!=null){
			stopwords.add(stopword);
			stopword = bufferedReader.readLine();
		}
		bufferedReader.close();
	}
	
	
	
	/*void getProperties(List<TransitiveGraph> graphs) throws JWNLException, IOException{
		

		double sum_size_n=0; double sum_size_e=0;
		//Set<String> graphSet = new HashSet<String>(GraphStorage.TransitiveGraphs.keySet());
		collection_graphNum = graphs.size();
		for (TransitiveGraph docGraph: graphs){
			
			
			//System.out.println(docGraph);
			NumGraphNodes0.add(docGraph.getVertices().size());
			NumGraphSeqs1.add(docGraph.getEdges().size());
			sum_size_n+=docGraph.getVertices().size();
			sum_size_e+=docGraph.getEdges().size();
			getProperties4Graph(docGraph);			
			
		}
		
		System.out.println("original mean size="+ sum_size_n/graphs.size()+","+sum_size_e/graphs.size());
		System.out.println("NumGraphwithLoop"+this.NumGraphwithLoop);
		
	}*/

	
void getProperties(List<SimpleGraph> graphs) throws JWNLException, IOException{
		

		double sum_size_n=0; double sum_size_e=0;
		//Set<String> graphSet = new HashSet<String>(GraphStorage.TransitiveGraphs.keySet());
		collection_graphNum = graphs.size();
		int i = 0;
		for (SimpleGraph docGraph: graphs){
			
			
			//System.out.println(docGraph);
			NumGraphNodes0.add(docGraph.getVertices().size());
			NumGraphSeqs1.add(docGraph.getEdges().size());
			sum_size_n+=docGraph.getVertices().size();
			sum_size_e+=docGraph.getEdges().size();
			getProperties4Graph(i,new TransitiveGraph(docGraph));	
			i++;
			
		}
		
		System.out.println("original mean size="+ sum_size_n/graphs.size()+","+sum_size_e/graphs.size());
		System.out.println("NumGraphwithLoop"+this.NumGraphwithLoop);
		/*for (String docModel: Matches.propertyTest){
			TransitiveGraph docGraph = storage.graphTransformer(Matches.prefix,docModel);
			getProperties4Graph(docGraph);
		}*/
	}
	
//static Set<Integer> nonGatewayPostSet = new HashSet<Integer>();
//static Set<Integer> GatewayPostSet = new HashSet<Integer>();
//static Set<Integer> nonGatewayPreSet = new HashSet<Integer>();
//static Set<Integer> GatewayPreSet = new HashSet<Integer>();

	void getProperties4Graph(int i, TransitiveGraph docGraph){
		
		Set<Integer> docNodes = new HashSet<Integer>(docGraph.getVertices());
		if(!docGraph.getBasicLoopVertices().isEmpty()||!docGraph.getTransitiveLoopVertices().isEmpty()){
			this.NumGraphwithLoop++;
			//System.out.println("Loop"+i+"size"+docNodes.size());
		}
		//System.out.println();
		for (Integer docNode: docNodes){
			
			
			String label = docGraph.getLabel(docNode);
			String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			//System.out.println(docNode+","+label+","+nodeType);
			
			if(nodeType!=null&&(nodeType.contains("Gateway")||nodeType.contains("gateway")||nodeType.contains("xor")||nodeType.contains("or")||nodeType.contains("and"))){ //gateways, label like "AND" and "XOR"
				GatewayList.add(label);
				GatewaySet.add(label);
				
				GatewayTypeSet.add(nodeType);
				GatewayTypeList.add(nodeType);
				List<String> words = getWordsInLabel(label);
				Set <Integer> preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
				Set <Integer> postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
				//if(!containsGateway(docGraph,postSet))
				getProperties4PreNodes(docGraph,docNode,words,preSet);
				
				//if(!containsGateway(docGraph,preSet))
				getProperties4PostNodes(docGraph,docNode,words,postSet);
				getSeqLength(docGraph,docNode, preSet, postSet);
				
				/*if(!containsGateway(docGraph,preSet)){
					nonGatewayPostSet = new HashSet<Integer>();
					GatewayPostSet = new HashSet<Integer>();
					getNonGatewayPostSet(docGraph,docNode);
					getProperties4PostNodes(docGraph,docNode,words,nonGatewayPostSet);
					System.out.print(nonGatewayPostSet.size()+"]");
				}
				
				if(!containsGateway(docGraph,postSet)){
					nonGatewayPreSet = new HashSet<Integer>();
					GatewayPreSet = new HashSet<Integer>();
					getNonGatewayPreSet(docGraph,docNode);
					getProperties4PreNodes(docGraph,docNode,words,nonGatewayPreSet);
					//System.out.print(nonGatewayPreSet.size()+"+");
				}*/
				
				
				
				
				
				
			}
			else{
				
				LabelList.add(label);
				LabelSet.add(label);
				List<String> words = getWordsInLabel(label);
				getProperties4Label(words);
				
				getProperties4Loop(docGraph, docNode, words);
				
				//
				/*Set<Integer> nonGatewayPreSet = new HashSet<Integer>();
				Set<Integer> GatewayPreSet = new HashSet<Integer>();
				getNonGatewayPreSet(nonGatewayPreSet,GatewayPreSet, docGraph,docNode);
				getProperties4PreNodes(docGraph,docNode,words,nonGatewayPreSet);
				
				Set<Integer> nonGatewayPostSet = new HashSet<Integer>();
				Set<Integer> GatewayPostSet = new HashSet<Integer>();
				getNonGatewayPostSet(nonGatewayPostSet,GatewayPostSet, docGraph,docNode);
				getProperties4PostNodes(docGraph,docNode,words,nonGatewayPostSet);*/
				
				Set <Integer> preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
				getProperties4PreNodes(docGraph,docNode,words,preSet);
				Set <Integer> postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
				getProperties4PostNodes(docGraph,docNode,words,postSet);
				getSeqLength(docGraph,docNode, preSet, postSet);
				
				//node type
				
				if(preSet.isEmpty()){
					if(nodeType.contains("boundaryEvent")){
						IntermediateNodeTypeSet.add(nodeType);
						IntermediateNodeTypeList.add(nodeType);
					}else{
						StartNodeTypeSet.add(nodeType);
						StartNodeTypeList.add(nodeType);
					}
					
				}
				if(postSet.isEmpty()){
					EndNodeTypeSet.add(nodeType);
					EndNodeTypeList.add(nodeType);
				}
				if(!preSet.isEmpty()&&!postSet.isEmpty()){
					IntermediateNodeTypeSet.add(nodeType);
					IntermediateNodeTypeList.add(nodeType);
				}
				
			}
			
			/*
			if(label.length()<=0)
				BlankLabelList.add(label);
			else if(label.length()<4){ //gateways, label like "AND" and "XOR"
				GatewayList.add(label.toLowerCase());
				GatewaySet.add(label.toLowerCase());
				
				GatewayTypeSet.add(nodeType);
				GatewayTypeList.add(nodeType);
			}
			else{
				
				LabelList.add(label);
				LabelSet.add(label);
				List<String> words = getWordsInLabel(label);
				getProperties4Label(words);
				
				getProerties4Loop(docGraph, docNode, words);
				
				Set <Integer> preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
				getProperties4PreNodes(docGraph,docNode,words,preSet);
				
				Set <Integer> postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
				getProperties4PostNodes(docGraph,docNode,words,postSet);
				
				getSeqLength(docGraph,docNode, preSet, postSet);
				
				//node type
				
				NodeTypeSet.add(nodeType);
				NodeTypeList.add(nodeType);
			}
			
			*/
		}
	}
	
	private boolean containsGateway(TransitiveGraph docGraph,
			Set<Integer> Set) {
		
	
		for(Integer i: Set){
			String nodeType = docGraph.getAttributeValue(i, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(nodeType!=null&&(nodeType.contains("Gateway")||nodeType.contains("xor")||nodeType.contains("or")||nodeType.contains("and"))){ //gateways, label like "AND" and "XOR"
				return true;
			}
		}
	
		return false;
	}

	/*static private void getNonGatewayPreSet( TransitiveGraph docGraph, Integer docNode) {
		
		
		Set<Integer> preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
		for(Integer i: preSet){
			String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(nodeType!=null&&(nodeType.toLowerCase().contains("event")||nodeType.toLowerCase().contains("function")||nodeType.toLowerCase().contains("task")||nodeType.toLowerCase().contains("activity")||nodeType.toLowerCase().contains("process"))){
				nonGatewayPreSet.add(i);
			}
			else if(nodeType!=null&&(nodeType.toLowerCase().contains("gateway")||nodeType.toLowerCase().contains("xor")||nodeType.toLowerCase().contains("or")||nodeType.toLowerCase().contains("and"))){ //gateways, label like "AND" and "XOR"
				
				if(!GatewayPreSet.contains(i)){
					getNonGatewayPreSet(docGraph,i);
				}
				GatewayPreSet.add(i);
			
			}
			
		}
		
	}*/
	
	/*static private void getNonGatewayPostSet( TransitiveGraph docGraph, Integer docNode) {
		
		
		Set<Integer> postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
		nonGatewayPostSet.addAll(postSet);
		System.out.print("{");
		for(Integer i: postSet){
			
			String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			//if(nodeType!=null&&(!nodeType.toLowerCase().contains("gateway")&&!nodeType.toLowerCase().contains("xor")&&!nodeType.toLowerCase().contains("or")&&!nodeType.toLowerCase().contains("and"))){ //gateways, label like "AND" and "XOR"
			System.out.print("<"+nodeType+">+");	
			//if(nodeType!=null&&(nodeType.toLowerCase().contains("event")||nodeType.toLowerCase().contains("function")||nodeType.toLowerCase().contains("task")||nodeType.toLowerCase().contains("activity")||nodeType.toLowerCase().contains("process"))){
			//	nonGatewayPostSet.add(i);
			//	System.out.print("-"+nonGatewayPostSet.size()+"-");	
			//}
			if(nodeType!=null&&(nodeType.toLowerCase().contains("gateway")||nodeType.toLowerCase().contains("xor")||nodeType.toLowerCase().contains("or")||nodeType.toLowerCase().contains("and"))){ //gateways, label like "AND" and "XOR"
				//nonGatewayPostSet.remove(i);
				System.out.print("-"+nonGatewayPostSet.size()+"-");	
				if(!GatewayPostSet.contains(i)){
					getNonGatewayPostSet(docGraph,i);
				}
				GatewayPostSet.add(i);
				System.out.print(":"+GatewayPostSet.size()+":");
			
			}
			
		}
		System.out.print("}");
	}*/

	private void getSeqLength(TransitiveGraph docGraph, Integer docNode,
			Set<Integer> preSet, Set<Integer> postSet) {
		
		int Length = 0;
		if(startSeq(docGraph, docNode, preSet)){
			Set<Integer> visited = new HashSet<Integer>();
			while(!endSeq(docGraph, docNode, postSet)&&!visited.contains(docNode)){
				
				visited.add(docNode);
				Length++;
				for(Integer node:postSet){
					docNode = node;
				}
				//preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
				postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
			}
			if(Length>1) SeqLength.add(Length);
		}
	}

	private boolean startSeq(TransitiveGraph docGraph, Integer docNode, Set<Integer> preSet) {
		
		boolean flag = false;
		//if(docGraph.getAttributeValue(docNode, TransitiveGraph.ATTRIBUTE_NODE_TYPE).toLowerCase().contains("gateway"))
			//return false;
		if (preSet.size()==0) return true;
		else if(preSet.size()>1)return true;
		
		else{	
			for (Integer preNode: preSet){
				int postEdge = docGraph.getBasicSuccessorsOfVertex(preNode).size();
				int preEdge = docGraph.getBasicPredecessorsOfVertex(preNode).size();
				if(postEdge!=1){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return flag;
	}
	
	private boolean endSeq(TransitiveGraph docGraph, Integer docNode, Set<Integer> postSet) {
		
		boolean flag = false;
		//if(docGraph.getAttributeValue(docNode, TransitiveGraph.ATTRIBUTE_NODE_TYPE).toLowerCase().contains("gateway"))
			//return false;
		if (postSet.size()==0) return true;
		else if(postSet.size()>1)return true;
		
		else{	
			for (Integer postNode: postSet){
				int postEdge = docGraph.getBasicSuccessorsOfVertex(postNode).size();
				int preEdge = docGraph.getBasicPredecessorsOfVertex(postNode).size();
				if(preEdge!=1){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return flag;
	}

	private void getProperties4Label(List<String> words){
		
			int stopWordCounter=0; int keyWordCounter=0;
			for(String w:words){
				if(stopwords.contains(w)){
					stopWordCounter++;
					StopWordList.add(w);
					StopWordSet.add(w);
				}
				else{
					keyWordCounter++;
					KeyWordList.add(w);
					KeyWordSet.add(w);
					List<String> keywords;
					if(WordsInCurrentLabel.containsKey(w))
						keywords = WordsInCurrentLabel.remove(w);
					else
						keywords = new ArrayList<String>();
					
					keywords.addAll(words);
					keywords.remove(w);
					WordsInCurrentLabel.put(w, keywords);
					List<Integer> wordNumList = new ArrayList<Integer>();
					wordNumList.add(words.size());
					if(WordNumInCurrentLabel4KeyWord.containsKey(w))
						wordNumList.addAll(WordNumInCurrentLabel4KeyWord.remove(w));
					WordNumInCurrentLabel4KeyWord.put(w, wordNumList);
				}
			}
			NumLabelKeyWords.add(keyWordCounter);
			NumLabelStopWords.add(stopWordCounter);
	}
	
	
	
	private void getProperties4Loop(TransitiveGraph docGraph, Integer docNode, List<String> words) {
		List <Integer> loopEdgeList = new ArrayList<Integer>();
		if(docGraph.getBasicLoopVertices().contains(docNode))
			loopEdgeList.add(-1);
		else if(docGraph.getTransitiveLoopVertices().contains(docNode))
			loopEdgeList.add(-2);
		else 
			return;
		if(loopEdgeList.size()>0)
			for(String word:words){
				if(LoopEdges4KeyWord.containsKey(word))
					LoopEdges4KeyWord.get(word).addAll(loopEdgeList);
				else
					LoopEdges4KeyWord.put(word, loopEdgeList);
			}
	}
	
	List<String> preKeyWordList = new ArrayList<String> ();
	
	private void getProperties4PreNodes(TransitiveGraph docGraph,Integer docNode,List<String> words,Set<Integer> preSet){
		
		List<String> keyWords = getKeyWords(words);
		preKeyWordList = new ArrayList<String> ();
		List<String> preKeyWordList = getPreKeyWordList(docGraph,docNode, preSet);
		List<Integer> inputEdgeList = getInputEdgeList(docGraph,docNode, preSet);
		List<Integer> outputEdgeList4PreviousNode = getOutputEdgeList4PreviousNode(docGraph, docNode,  preSet);
		
		for(String keyWord:keyWords){
			//store words in the previous label
			
			if(preKeyWordList.size()>0){
				if(KeyWordsInPreLabel.containsKey(keyWord))
					KeyWordsInPreLabel.get(keyWord).addAll(preKeyWordList);
				else
					KeyWordsInPreLabel.put(keyWord, preKeyWordList);
			}
			//store # of input edges for the word
			
			if(inputEdgeList.size()>0){
				if(InputEdges4KeyWord.containsKey(keyWord))
					InputEdges4KeyWord.get(keyWord).addAll(inputEdgeList);
				else
					InputEdges4KeyWord.put(keyWord, inputEdgeList);
			}
			//store # of oupput edges of previous node for the word
			
			if(outputEdgeList4PreviousNode.size()>0){
				if(OutputEdgesofPreviousNode4KeyWord.containsKey(keyWord))
					OutputEdgesofPreviousNode4KeyWord.get(keyWord).addAll(outputEdgeList4PreviousNode);
				else
					OutputEdgesofPreviousNode4KeyWord.put(keyWord, outputEdgeList4PreviousNode);
			}
		}
		
		
		getNodeTypeListforPreNodes(docGraph, docNode);
		
		
	}
	
	private void getNodeTypeListforPreNodes(TransitiveGraph docGraph,Integer docNode){
		String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
		Set<Integer> preSet = docGraph.getBasicPredecessorsOfVertex(docNode);
		if (preSet.isEmpty()) 
			return;
		for(Integer n:preSet){
			String preNodeType = docGraph.getAttributeValue(n, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(preNodeType.contains("Gateway"))
				getNodeTypeListforPreNodes(docGraph, n);
			else if(!preNodeType.contains("start")&&!preNodeType.contains("end")){
				
				if(!NodeTypeInPreNodes.containsKey(nodeType))
					NodeTypeInPreNodes.put(nodeType,new ArrayList<String>());			
				NodeTypeInPreNodes.get(nodeType).add(preNodeType);	
				//consider gateways
				
			}
			

		}
	}
	
	List<String> postKeyWordList = new ArrayList<String> ();
	
	private void getProperties4PostNodes(TransitiveGraph docGraph,Integer docNode,List<String> words,Set<Integer> postSet){
		List<String> keyWords = getKeyWords(words);
		
		postKeyWordList = new ArrayList<String> ();
		List<String> postKeyWordList = getPostKeyWordList(docGraph,docNode, postSet);
		List<Integer> outputEdgeList = getOutputEdgeList(docGraph,docNode, postSet);
		List<Integer> inputEdgeList4NextNode = getInputEdgeList4NextNode(docGraph, docNode,  postSet);
		
		for(String keyWord:keyWords){
			//store words in the next label
			
			if(!postKeyWordList.isEmpty()){
				if(KeyWordsInPostLabel.containsKey(keyWord))
					KeyWordsInPostLabel.get(keyWord).addAll(postKeyWordList);
				else
					KeyWordsInPostLabel.put(keyWord, postKeyWordList);
			}
			//store # of output edges for the word
			
			if(!outputEdgeList.isEmpty()){
				if(OutputEdges4KeyWord.containsKey(keyWord))
					OutputEdges4KeyWord.get(keyWord).addAll(outputEdgeList);
				else
					OutputEdges4KeyWord.put(keyWord, outputEdgeList);
			}
			//store # of input edges of next node for the word
			
			if(!inputEdgeList4NextNode.isEmpty()){
				if(InputEdgesofNextNode4KeyWord.containsKey(keyWord))
					InputEdgesofNextNode4KeyWord.get(keyWord).addAll(inputEdgeList4NextNode);
				else
					InputEdgesofNextNode4KeyWord.put(keyWord, inputEdgeList4NextNode);
			}
		}
		
		/*String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
		for(Integer n:postSet){
			if(!NodeTypeInPostNodes.containsKey(nodeType))
				NodeTypeInPostNodes.put(nodeType,new ArrayList<String>());			
			NodeTypeInPostNodes.get(nodeType).add(docGraph.getAttributeValue(n, SimpleGraph.ATTRIBUTE_NODE_TYPE));	

		}*/
		getNodeTypeListforPostNodes(docGraph, docNode);
		
		
	}
	
	private void getNodeTypeListforPostNodes(TransitiveGraph docGraph,Integer docNode){
		String nodeType = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
		Set<Integer> postSet = docGraph.getBasicSuccessorsOfVertex(docNode);
		if (postSet.isEmpty()) 
			return;
		for(Integer n:postSet){
			String postNodeType = docGraph.getAttributeValue(n, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(postNodeType.contains("Gateway"))
				getNodeTypeListforPostNodes(docGraph, n);
			else if(!postNodeType.contains("start")&&!postNodeType.contains("end")){
				if(!NodeTypeInPostNodes.containsKey(nodeType))
					NodeTypeInPostNodes.put(nodeType,new ArrayList<String>());			
				NodeTypeInPostNodes.get(nodeType).add(postNodeType);	
				
			}
			

		}
	}
	
	private List<String> getKeyWords(List<String> words){
		List<String> tempList = new ArrayList<String>();
		for(String word:words)
			if(!stopwords.contains(word)&&!(word.contains("Gateway")||word.contains("xor")||word.contains("or")||word.contains("and"))){
				tempList.add(word);
			}
		return tempList;
	}
	
	private List<String> getPreKeyWordList(TransitiveGraph docGraph,Integer docNode, Set<Integer> preSet){
		// get key words from previous labels. skip labels like "AND" and "XOR" by considering their previous labels instead.
		
		if(!preSet.isEmpty())
		for (Integer preNode: preSet){
			String preLabel = docGraph.getLabel(preNode);
			List<String> preWords = getWordsInLabel(preLabel);
			String type = docGraph.getAttributeValue(preNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			//preKeyWordList.addAll(getKeyWords(preWords));
			if(type.contains("Gateway")||type.contains("xor")||type.contains("or")||type.contains("and")){//labels like "AND" and "XOR"
				for(Integer prepreNode : docGraph.getBasicPredecessorsOfVertex(preNode)){
					String prepreLabel = docGraph.getLabel(prepreNode);
					List<String> prepreWords = getWordsInLabel(prepreLabel);
					preKeyWordList.addAll(getKeyWords(prepreWords));
				}
					
			}
				
			else
				preKeyWordList.addAll(getKeyWords(preWords));	
		}
		return preKeyWordList;
	}
	
	private List<String> getPostKeyWordList(TransitiveGraph docGraph,
			Integer docNode, Set<Integer> postSet) {
		
		if(!postSet.isEmpty())
		for (Integer postNode: postSet){
			String postLabel = docGraph.getLabel(postNode);
			List<String> postWords = getWordsInLabel(postLabel);
			//postKeyWordList.addAll(getKeyWords(postWords));	
			String type = docGraph.getAttributeValue(postNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(type.contains("Gateway")||type.contains("xor")||type.contains("or")||type.contains("and")){//labels like "AND" and "XOR"
				//if(!docGraph.getBasicSuccessorsOfVertex(postNode).isEmpty())
					//postKeyWordList.addAll(getPostKeyWordList(docGraph, postNode, docGraph.getBasicSuccessorsOfVertex(postNode)));
				for(Integer postpostNode : docGraph.getBasicSuccessorsOfVertex(postNode)){
					String postpostLabel = docGraph.getLabel(postpostNode);
					List<String> prepreWords = getWordsInLabel(postpostLabel);
					preKeyWordList.addAll(getKeyWords(prepreWords));
				}
			}
				
			else
				postKeyWordList.addAll(getKeyWords(postWords));	
		}
		return postKeyWordList;
	}
	
	
	private List<Integer> getInputEdgeList(TransitiveGraph docGraph,Integer docNode, Set<Integer> preSet){
		List<Integer> inputEdgeList = new ArrayList<Integer>();
		String gateway = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
		if(preSet.size()==1)
			for (Integer preNode: preSet){
				//String preLabel = docGraph.getLabel(preNode);
				//List<String> preWords = getWordsInLabel(preLabel);
				//if(preLabel.toLowerCase()=="and"||preLabel.toLowerCase()=="xor")//
				String type = docGraph.getAttributeValue(preNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
				if(type.contains("Gateway")){//labels like "AND" and "XOR"
					//preSet = docGraph.getBasicPredecessorsOfVertex(preNode);
					//gateway = type;
				}
				else
					inputEdgeList.add(preSet.size());
			}
		if(preSet.size()>1){
			if(docGraph.getBasicLoopVertices().contains(docNode)||docGraph.getTransitiveLoopVertices().contains(docNode)){
				//inputEdgeList.add(preSet.size());//loop
				inputEdgeList.add(preSet.size()-1);//loop
			}
			else{
				inputEdgeList.add(preSet.size());
				//inputEdgeList.add(-1);
			}
		}
		/*if(!gateway.contains("gateway")){
			Random r = new Random();
			gateway = GatewayTypeList.get(r.nextInt(GatewayTypeList.size()));
			
		}*/
		if(preSet.size()>1)
			recordJoinPattern(docGraph, preSet, gateway);
		return inputEdgeList;
	}
	
	private List<Integer> getOutputEdgeList(TransitiveGraph docGraph,
			Integer docNode, Set<Integer> postSet) {
		List<Integer> outputEdgeList = new ArrayList<Integer>();
		String gateway = docGraph.getAttributeValue(docNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
		if(postSet.size()==1)
			for (Integer postNode: postSet){
				//String postLabel = docGraph.getLabel(postNode);
				//List<String> postWords = getWordsInLabel(postLabel);
				//if(postLabel.toLowerCase()=="and"||postLabel.toLowerCase()=="xor")//
				String type = docGraph.getAttributeValue(postNode, SimpleGraph.ATTRIBUTE_NODE_TYPE);
				if(type.contains("Gateway")){//labels like "AND" and "XOR"
					//postSet = docGraph.getBasicSuccessorsOfVertex(postNode);
					//gateway = type;
				}else
					outputEdgeList.add(postSet.size());
			}
		if(postSet.size()>1){
			if(docGraph.getBasicLoopVertices().contains(docNode)||docGraph.getTransitiveLoopVertices().contains(docNode)){
				//outputEdgeList.add(postSet.size());//loop
				outputEdgeList.add(postSet.size()-1);//loop
			}
			else{
				outputEdgeList.add(postSet.size());
				//outputEdgeList.add(-1);
			}
		}
		/*if(!gateway.contains("gateway")){
			Random r = new Random();
			gateway = GatewayTypeList.get(r.nextInt(GatewayTypeList.size()));
			
		}*/
		
		if(postSet.size()>1)
			recordSplitPattern(docGraph, postSet, gateway);
		return outputEdgeList;
	}
	
	private void recordJoinPattern(TransitiveGraph docGraph,
			Set<Integer> preSet, String gateway) {
		
		Set<Integer> disConnections = new HashSet<Integer>(preSet);
		Pattern4MultiNodes joinPattern = new Pattern4MultiNodes();
		joinPattern.gateway = gateway;
		for(Integer id: preSet)
			joinPattern.nodeList.add(id);
		recordPreConnections(docGraph, preSet, "basic", disConnections, joinPattern);
		recordPreConnections(docGraph, preSet, "transitive", disConnections, joinPattern);
		recordPreDisConnections(docGraph, disConnections, joinPattern);
		int key = preSet.size();
		putPatternInHashtable(JoinPatterns, key, joinPattern);
		if(joinPattern.terminativeNodes.size()!=preSet.size()){
			putPatternInHashtable(nonterminativeJoinPatterns, key, joinPattern);
		}
		
		
		//Hashtable<Integer,List<Pattern4MultiNodes>> JoinPatterns;
		
		
	}
	
	private void putPatternInHashtable(Hashtable<Integer,List<Pattern4MultiNodes>> patterns, int key, Pattern4MultiNodes pattern){
		if(patterns.containsKey(key))
			patterns.get(key).add(pattern);
		else{
			List<Pattern4MultiNodes> temp = new LinkedList<Pattern4MultiNodes>();
			temp.add(pattern);
			patterns.put(key, temp);
		}
	}
	
	private void recordSplitPattern(TransitiveGraph docGraph,
			Set<Integer> postSet, String gateway) {
		
		Set<Integer> disConnections = new HashSet<Integer>(postSet);
		
		Pattern4MultiNodes splitPattern = new Pattern4MultiNodes();
		splitPattern.gateway = gateway;
		for(Integer id: postSet)
			splitPattern.nodeList.add(id);
		recordPostConnections(docGraph, postSet, "basic", disConnections, splitPattern);
		recordPostConnections(docGraph, postSet, "transitive", disConnections, splitPattern);
		recordPostDisConnections(docGraph, disConnections, splitPattern);
		int key = postSet.size();
		
		putPatternInHashtable(SplitPatterns, key, splitPattern);
		if(splitPattern.terminativeNodes.size()!=postSet.size()){
			putPatternInHashtable(nonterminativeSplitPatterns, key, splitPattern);
		}
		
		/*if(SplitPatterns.containsKey(key))
			SplitPatterns.get(key).add(splitPattern);
		else{
			List<Pattern4MultiNodes> temp = new LinkedList<Pattern4MultiNodes>();
			temp.add(splitPattern);
			SplitPatterns.put(key, temp);
		}*/
		
	}



	private void recordPreConnections(TransitiveGraph docGraph,
			Set<Integer> preSet, String type, Set<Integer> disconnections, Pattern4MultiNodes joinPattern) {

		Hashtable <Integer,HashSet<Integer>> preNodeMap = new Hashtable <Integer,HashSet<Integer>>();
		//prePreNode, preNodes
	
		Set<Integer> prepreSet = new HashSet<Integer>();;
		
		for(Integer preNode: preSet){
			if(type=="basic"){
				prepreSet = docGraph.getBasicPredecessorsOfVertex(preNode);
				
				
				//nonGatewayPreSet = new HashSet<Integer>();
				//GatewayPreSet = new HashSet<Integer>();
				//getNonGatewayPreSet(docGraph,preNode);
				//prepreSet.addAll(nonGatewayPostSet);
				
				
			}
				
			else
				prepreSet = docGraph.getAllPredecessorsOfVertex(preNode);
			
			for(Integer prePreNode: prepreSet){
				if(preNodeMap.containsKey(prePreNode))
					preNodeMap.get(prePreNode).add(preNode);
				else{
					HashSet<Integer> temp = new HashSet<Integer>();
					temp.add(preNode);
					preNodeMap.put(prePreNode, temp);
				}	
			}
		}
		boolean flag = false;
		for(Integer prePreNode: preNodeMap.keySet()){
			HashSet<Integer> splitNodes = preNodeMap.get(prePreNode);
			if(splitNodes.size()>1){
				if(type=="basic"){
					joinPattern.basicConnections.add(splitNodes);
					//joinPattern.Connection2Nodes.put(prePreNode, splitNodes);
				}
				else if(!joinPattern.basicConnections.contains(splitNodes)){
					flag = true;
					for(HashSet<Integer> nodes : joinPattern.basicConnections){
						if(nodes.contains(splitNodes))
							flag = false;
					}
					if(flag)
						joinPattern.transitiveConnections.add(splitNodes);
				}
					
				disconnections.removeAll(splitNodes);
			}
			
		}
	}

	private void recordPostConnections(TransitiveGraph docGraph,
			Set<Integer> postSet, String type, Set<Integer> disConnections,
			Pattern4MultiNodes splitPattern) {
		
		Hashtable <Integer,HashSet<Integer>> postNodeMap = new Hashtable <Integer,HashSet<Integer>>();
		//postPostNode, postNodes
	
		Set<Integer> postpostSet;
		
		for(Integer postNode: postSet){
			if(type=="basic"){
				postpostSet = docGraph.getBasicSuccessorsOfVertex(postNode);
				
				//postpostSet = new HashSet<Integer>();
				
				//nonGatewayPostSet = new HashSet<Integer>();
				//GatewayPostSet = new HashSet<Integer>();
				//getNonGatewayPostSet(docGraph,postNode);
				//postpostSet.addAll(nonGatewayPostSet);
				
								
			}
			else
				postpostSet = docGraph.getAllSuccessorsOfVertex(postNode);
			
			for(Integer postpostNode: postpostSet){
				if(postNodeMap.containsKey(postpostNode))
					postNodeMap.get(postpostNode).add(postNode);
				else{
					HashSet<Integer> temp = new HashSet<Integer>();
					temp.add(postNode);
					postNodeMap.put(postpostNode, temp);
				}	
			}
		}
		boolean flag = false;
		for(Integer postpostNode: postNodeMap.keySet()){
			HashSet<Integer> joinNodes = postNodeMap.get(postpostNode);
			if(joinNodes.size()>1){
				if(type=="basic")
					splitPattern.basicConnections.add(joinNodes);
				else if(!splitPattern.basicConnections.contains(joinNodes)){
					flag = true;
					for(HashSet<Integer> nodes : splitPattern.basicConnections){
						if(nodes.contains(joinNodes))
							flag = false;
					}
					if(flag)
						splitPattern.transitiveConnections.add(joinNodes);
				}
				disConnections.removeAll(joinNodes);
			}
			
		}
	}
	
	private void recordPreDisConnections(TransitiveGraph docGraph,
			Set<Integer> disConnections,
			Pattern4MultiNodes joinPattern) {
		
		for(Integer preNode:disConnections){
			if(docGraph.getBasicPredecessorsOfVertex(preNode).isEmpty())
				joinPattern.terminativeNodes.add(preNode);
			else
				joinPattern.separateNodes.add(preNode);
		}
	}
	


	private void recordPostDisConnections(TransitiveGraph docGraph,
			Set<Integer> disConnections,
			Pattern4MultiNodes splitPattern) {
		
		for(Integer postNode:disConnections){
			if(docGraph.getBasicSuccessorsOfVertex(postNode).isEmpty())
				splitPattern.terminativeNodes.add(postNode);
			else
				splitPattern.separateNodes.add(postNode);
		}
		
	}

	

	private List<Integer> getOutputEdgeList4PreviousNode(TransitiveGraph docGraph,Integer docNode, Set<Integer> preSet){
		List<Integer> outputEdgeList4PreviousNode = new ArrayList<Integer>();
		for (Integer preNode: preSet){
			int edge = docGraph.getBasicSuccessorsOfVertex(preNode).size();
			//int edge;
			//nonGatewayPostSet = new HashSet<Integer>();
			//GatewayPostSet = new HashSet<Integer>();
			//getNonGatewayPostSet(docGraph,docNode);
			//edge = nonGatewayPostSet.size();
			
			if(edge>1)
				outputEdgeList4PreviousNode.add(edge-1);
		}
		return outputEdgeList4PreviousNode;
	}
	private List<Integer> getInputEdgeList4NextNode(TransitiveGraph docGraph,
			Integer docNode, Set<Integer> postSet) {
		List<Integer> inputEdgeList4NextNode = new ArrayList<Integer>();
		for (Integer postNode: postSet){
			int edge = docGraph.getBasicPredecessorsOfVertex(postNode).size();
			//int edge;
			//nonGatewayPreSet = new HashSet<Integer>();
			//GatewayPreSet = new HashSet<Integer>();
			//getNonGatewayPreSet(docGraph,docNode);
			//edge = nonGatewayPreSet.size();
			if(edge>1)
				inputEdgeList4NextNode.add(edge-1);
		}
		return inputEdgeList4NextNode;
	}
	
	private boolean checkGatewayLabel(String label){
		List<String> words = getWordsInLabel(label);
		return words.size()<=1;
	}
	
	private List<String> getWordsInLabel(String label) {
		List<String> words = new ArrayList<String>();
		String word;
		Boolean start = false;
		int startI = 0;
		for(int i=0; i<label.length(); i++){
			//start = true;
			if(Character.isLetter(label.charAt(i))&&!start){
				startI = i;
				start = true;
			}
			else if(!Character.isLetter(label.charAt(i))&&label.charAt(i)!='\''&&start){
				word = label.substring(startI, i).toLowerCase();
				words.add(word);
				start = false;
				if(label.charAt(i)=='\\'){
					i++;	
				}				
			}
			if(i==label.length()-1&&start){
				//words.add(label.substring(startI, i+1).toLowerCase());
				word = label.substring(startI, i+1).toLowerCase();
				//word = WordNetHandler.getDict().lookupAllIndexWords(word).getLemma();				
				words.add(word);				
			}
		}
		return words;
	}
	
	public static void main(String[] args) throws IOException, JWNLException{
		GraphCollectionProperty gcp = new GraphCollectionProperty();
		
		
		
	}
	
	
	
}
