package nl.tue.tm.is.syntheticgraph;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


import net.didion.jwnl.JWNLException;
import nl.tue.tm.is.graph.SimpleGraph;


public class SyntheticGraph {
	
	List<SimpleGraph> syntheticGraphList; 
	List<SimpleGraph> syntheticGraphListwithLoop;
	
	Hashtable<String,List<String>> postWordList4Label;
			//current label, post word List
	Hashtable<String,List<Integer>> outputEdgeList4Label;
	Hashtable<String,List<Integer>> inputEdgesofNextNodeList4Label;
	
	Hashtable<String,List<String>> preWordList4Label;
	//current label, post word List
	Hashtable<String,List<Integer>> inputEdgeList4Label;
	Hashtable<String,List<Integer>> outputEdgesofPreviousNodeList4Label;
	
	Hashtable<String,List<Integer>> loopList4Label;
	
	GraphCollectionProperty tempgcp;
	
	//int buffersize;
	SyntheticGraph() throws IOException{
		
		syntheticGraphList = new ArrayList<SimpleGraph>(); 
		syntheticGraphListwithLoop = new ArrayList<SimpleGraph>();
		//buffersize = 2;
		
		postWordList4Label = new Hashtable<String,List<String>> ();
		//current label, post word List
		outputEdgeList4Label = new Hashtable<String,List<Integer>> () ;
		inputEdgesofNextNodeList4Label = new Hashtable<String,List<Integer>> ();

		preWordList4Label = new Hashtable<String,List<String>> ();
						//current label, post word List
		inputEdgeList4Label = new Hashtable<String,List<Integer>> ();
		outputEdgesofPreviousNodeList4Label = new Hashtable<String,List<Integer>> ();

		loopList4Label = new Hashtable<String,List<Integer>> ();
	}
	
	SyntheticGraph(SyntheticGraph sg){
		syntheticGraphList = new ArrayList<SimpleGraph>(); 
		syntheticGraphList.addAll(sg.syntheticGraphList);
		syntheticGraphListwithLoop = new ArrayList<SimpleGraph>();
		syntheticGraphListwithLoop.addAll(sg.syntheticGraphListwithLoop);
		//buffersize = 2;
		
		postWordList4Label = new Hashtable<String,List<String>> ();
		postWordList4Label=(Hashtable<String, List<String>>) (sg.postWordList4Label.clone());
		//current label, post word List
		outputEdgeList4Label = new Hashtable<String,List<Integer>> () ;
		outputEdgeList4Label = (Hashtable<String, List<Integer>>) sg.outputEdgeList4Label.clone();
		inputEdgesofNextNodeList4Label = new Hashtable<String,List<Integer>> ();
		inputEdgesofNextNodeList4Label = (Hashtable<String, List<Integer>>) sg.inputEdgesofNextNodeList4Label.clone();

		preWordList4Label = new Hashtable<String,List<String>> ();
		preWordList4Label = (Hashtable<String, List<String>>) sg.preWordList4Label.clone();
						//current label, post word List
		inputEdgeList4Label = new Hashtable<String,List<Integer>> ();
		inputEdgeList4Label = (Hashtable<String, List<Integer>>) sg.inputEdgeList4Label.clone();
		
		outputEdgesofPreviousNodeList4Label = new Hashtable<String,List<Integer>> ();
		outputEdgesofPreviousNodeList4Label = (Hashtable<String, List<Integer>>) sg.outputEdgesofPreviousNodeList4Label.clone();

		loopList4Label = new Hashtable<String,List<Integer>> ();
		loopList4Label = (Hashtable<String, List<Integer>>) sg.loopList4Label.clone();
	}
	
	
	void reset(SyntheticGraph sg){
		syntheticGraphList = new ArrayList<SimpleGraph>(); 
		syntheticGraphList.addAll(sg.syntheticGraphList);
		syntheticGraphListwithLoop = new ArrayList<SimpleGraph>();
		syntheticGraphListwithLoop.addAll(sg.syntheticGraphListwithLoop);
		//buffersize = 2;
		
		postWordList4Label = new Hashtable<String,List<String>> ();
		postWordList4Label=(Hashtable<String, List<String>>) (sg.postWordList4Label.clone());
		//current label, post word List
		outputEdgeList4Label = new Hashtable<String,List<Integer>> () ;
		outputEdgeList4Label = (Hashtable<String, List<Integer>>) sg.outputEdgeList4Label.clone();
		inputEdgesofNextNodeList4Label = new Hashtable<String,List<Integer>> ();
		inputEdgesofNextNodeList4Label = (Hashtable<String, List<Integer>>) sg.inputEdgesofNextNodeList4Label.clone();

		preWordList4Label = new Hashtable<String,List<String>> ();
		preWordList4Label = (Hashtable<String, List<String>>) sg.preWordList4Label.clone();
						//current label, post word List
		inputEdgeList4Label = new Hashtable<String,List<Integer>> ();
		inputEdgeList4Label = (Hashtable<String, List<Integer>>) sg.inputEdgeList4Label.clone();
		
		outputEdgesofPreviousNodeList4Label = new Hashtable<String,List<Integer>> ();
		outputEdgesofPreviousNodeList4Label = (Hashtable<String, List<Integer>>) sg.outputEdgesofPreviousNodeList4Label.clone();

		loopList4Label = new Hashtable<String,List<Integer>> ();
		loopList4Label = (Hashtable<String, List<Integer>>) sg.loopList4Label.clone();
	}
	
	List<SimpleGraph> generateCollection(GraphCollectionProperty gcp, SyntheticFragment sf, boolean considerGateway) throws IOException, JWNLException, CloneNotSupportedException{
		//int collectionSize;
		Random r = new Random();
		
		//System.out.println(sf.KeyWord2Label);
		//for(int size: gcp.NumGraphNodes0)
		//System.out.println("randomgraphsize");
		//double procsizes[] = CreateRandomGraphs.getDistro(gcp.NumGraphNodes0.size(),21.6,15,true);
		//System.out.println("randomgraphsizedone");
		long start = System.currentTimeMillis();
		tempgcp = new GraphCollectionProperty(gcp);
		for(int i=0; i<gcp.NumGraphNodes0.size(); i++){
			
			GraphCollectionProperty gcp1 = new GraphCollectionProperty(gcp);
			//gcp.
			SyntheticFragment sf1 = new SyntheticFragment(sf);
			SyntheticGraph sg1 = new SyntheticGraph(this);
			
			
			SimpleGraph g = generateGraph(gcp.NumGraphNodes0.get(i),r,gcp,sf, considerGateway);
			if(g==null){
				
				//System.out.println(sf1.LabelList==sf.LabelList);
				//System.out.println(sf1.LabelList.size()+">>>"+sf.LabelList.size());
				
				gcp = gcp1;
				sf = sf1;
				this.reset(sg1);
				
				
				
				//gcp = new GraphCollectionProperity(gcp1);
				//sf = new SyntheticFragment(sf1);
				
				//System.out.println(sf1.LabelList==sf.LabelList);
				//System.out.println(sf1.LabelList.size()+">>>"+sf.LabelList.size());
				
				//System.out.println("test end!");
				
				
				
				i--;
			}
			else{
				syntheticGraphList.add(g);
				//System.out.println(i);
			}
			//System.out.println(i);
			//System.out.println("graphsize:"+g.getVertices().size());
		}
		long time = System.currentTimeMillis()-start;
		System.out.println(time);
		System.out.println("syntheticGraphListwithLoop"+syntheticGraphListwithLoop.size());
		return syntheticGraphList;
	}

	private SimpleGraph generateGraph(Integer numGraphNode, Random r, GraphCollectionProperty gcp, SyntheticFragment sf, boolean considerGateway) throws IOException {
		
		List<Integer> preOpenidSet = new ArrayList<Integer>();
		List<Integer> postOpenidSet = new ArrayList<Integer>();
		Set<Integer> preLoopOpenidSet = new HashSet<Integer>();
		Set<Integer> postLoopOpenidSet = new HashSet<Integer>();
		
		
		//GraphCollectionProperity gcp1 = new GraphCollectionProperity(gcp);
		//SyntheticFragment sf1 = new SyntheticFragment(sf);
		
		
		SimpleGraph g = new SimpleGraph();
		String label = sf.LabelList.get(r.nextInt(sf.LabelList.size()));
		int newid = g.addVertex(label);
		//while(r.nextInt(gcp.NodeTypeList.size()))
		if(gcp.IntermediateNodeTypeList.isEmpty())
			gcp.IntermediateNodeTypeList.addAll(tempgcp.IntermediateNodeTypeList);
		g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.IntermediateNodeTypeList.get(r.nextInt(gcp.IntermediateNodeTypeList.size())));
		
		preOpenidSet.add(newid);
		postOpenidSet.add(newid);
		/*List<String> wordList = preWordList4Label.get(label);
		
		List<Integer> ids = extendPreNodes(r, gcp, sf, g, newid, 1, wordList);
		preOpenidSet.addAll(ids);*/
		//Set<Integer> tempPreOpenidSet = new HashSet<Integer>(preOpenidSet);
		//Set<Integer> tempPostOpenidSet = new HashSet<Integer>(postOpenidSet);
		//double difference = numGraphNode*0.05;
		//double difference = 5;
		double difference = (numGraphNode*0.1)>3?(numGraphNode*0.1):3;
		//int count = 0;
		int j = 0;
		while(!preOpenidSet.isEmpty()&&!postOpenidSet.isEmpty()&&j<100){
			//count++;
			//System.out.println("count!!!!!!!!!!"+count);
		//while(!tempPreOpenidSet.isEmpty()&&!tempPostOpenidSet.isEmpty()){	
			int i = r.nextInt(preOpenidSet.size()+postOpenidSet.size());
			if(i<preOpenidSet.size()){
				int id = preOpenidSet.remove(i);
				//System.out.print(";;;open pre id:"+id);
				extendPreStructure(r, gcp, sf, g, id, preOpenidSet, postOpenidSet, preLoopOpenidSet);
				j++;
			}
			else{
				int id = postOpenidSet.remove(i-preOpenidSet.size());
				//System.out.print(";;;open post id:"+id);
				
				extendPostStructure(r, gcp, sf, g, id, preOpenidSet, postOpenidSet, postLoopOpenidSet);
				j++;
			}
				
				//System.out.println("post set of id:"+g.getSuccessorsOfVertex(id));
				//System.out.println("post open list:"+postOpenidSet);
				//System.out.println("post");
				//System.out.println(postOpenidSet);
				//System.out.println(g.getVertices());
			
				
				//System.out.println("pre set of id:"+g.getPredecessorsOfVertex(id));
				//System.out.println("pre open list:"+preOpenidSet);
				//System.out.println("pre");
				//System.out.println(preOpenidSet);
				//System.out.println(g.getVertices());
			
			//tempPreOpenidSet = new HashSet<Integer>(preOpenidSet);
			//tempPostOpenidSet = new HashSet<Integer>(postOpenidSet);
			//double graphSize = g.getVertices().size();
			int nodeNum = g.getVertices().size();
			if(!considerGateway){
				for (Integer v: g.getVertices()){
				if(g.getAttributeValue(v, SimpleGraph.ATTRIBUTE_NODE_TYPE).contains("Gateway"))
					nodeNum--;
				}
			}
			
			
			if(nodeNum>=numGraphNode-difference)
				break;
			//System.out.println("while");
		}		
		//System.out.println("end");
		
		/*if(!considerGateway){
			Set<Integer> silentVertices = new HashSet<Integer>();
			for (Integer v: g.getVertices()){
				if (g.getLabel(v).length()<4){
					silentVertices.add(v);
				}
			}
			//System.out.println(silentVertices);
			g = g.removeVertices(silentVertices);
		}*/
		
		if(j>=numGraphNode) return null;
		
		if(g.getVertices().size()-numGraphNode<=difference&&g.getVertices().size()-numGraphNode>=-difference){//valid graph
		//if(g.getVertices().size()>=numGraphNode-difference){
			extendLoopStructure(g, preLoopOpenidSet, postLoopOpenidSet);
			return g;
		}
		else{
			//gcp = gcp1;
			//sf = sf1;
			return null;
		}
		//return g;
		
	}

	private void extendLoopStructure(SimpleGraph g,
			Set<Integer> preLoopOpenidSet, Set<Integer> postLoopOpenidSet) {
		
		Random r = new Random();
		List<Integer> openidSet = new ArrayList<Integer>();
		openidSet.addAll(postLoopOpenidSet);
		openidSet.addAll(preLoopOpenidSet);
		
		//if(openidSet.size()>1&&!preLoopOpenidSet.isEmpty()&&!postLoopOpenidSet.isEmpty()){
		if(openidSet.size()>1){
		//if(preLoopOpenidSet.size()>0&&postLoopOpenidSet.size()>0&&(preLoopOpenidSet.size()+postLoopOpenidSet.size())>2){
			int i = r.nextInt(openidSet.size());
			int source = openidSet.remove(i);
			i = r.nextInt(openidSet.size());
			int target = openidSet.remove(i);
			if(!g.existPath(source, target))
				g.addEdge(source, target);
			if(!g.existPath(target, source))
				g.addEdge(target,source);
			/*for(Integer source: postLoopOpenidSet)
				for(Integer target: preLoopOpenidSet){
					if(!g.existPath(source, target))
						g.addEdge(source, target);
					if(!g.existPath(target, source))
						g.addEdge(target,source);
				}*/
			syntheticGraphListwithLoop.add(g);
		}
		
		
	}

	private void extendPostStructure(Random r, GraphCollectionProperty gcp,
			SyntheticFragment sf, SimpleGraph g, Integer id, List<Integer> preOpenidSet,
			List<Integer> postOpenidSet, Set<Integer> loopOpenidSet) {
		
		String label = g.getLabel(id);
		//if(id<10||(id<1010&&id>1000))
		//System.out.println(id+";post;"+label);
		/*List<String> wordList = new ArrayList<String>();
		List<Integer> outputEdgeList = new ArrayList<Integer>();
		List<Integer> inputEdgesofNextNodeList = new ArrayList<Integer>();
		List<Integer> loopList = new ArrayList<Integer>();
		for(String word:label.split(" ")){
			if(gcp.StopWordSet.contains(word))
				continue;
			if(gcp.KeyWordsInPostLabel.containsKey(word))
				wordList.addAll(gcp.KeyWordsInPostLabel.get(word));
			if(gcp.OutputEdges4KeyWord.containsKey(word))
				outputEdgeList.addAll(gcp.OutputEdges4KeyWord.get(word));
			//if(gcp.InputEdgesofNextNode4KeyWord.containsKey(word))
				//inputEdgesofNextNodeList.addAll(gcp.InputEdgesofNextNode4KeyWord.get(word));
			if(gcp.LoopEdges4KeyWord.containsKey(word))
				loopList.addAll(gcp.LoopEdges4KeyWord.get(word));
		}*/
		
		//System.out.println("PostWordList;"+wordList);
		
		/*List<String> wordList = postWordList4Label.get(label);
		List<Integer> outputEdgeList = outputEdgeList4Label.get(label);
		List<Integer> inputEdgesofNextNodeList = inputEdgesofNextNodeList4Label.get(label);
		List<Integer> loopList = loopList4Label.get(label);*/
		
		List<String> wordList = new ArrayList<String>();
		if(postWordList4Label.containsKey(label))
			wordList = postWordList4Label.get(label);
		List<Integer> outputEdgeList = new ArrayList<Integer>();
		if(outputEdgeList4Label.containsKey(label))
			outputEdgeList = outputEdgeList4Label.get(label);
		List<Integer> inputEdgesofNextNodeList = new ArrayList<Integer>();
		if(inputEdgesofNextNodeList4Label.containsKey(label))
			inputEdgesofNextNodeList = inputEdgesofNextNodeList4Label.get(label);
		List<Integer> loopList = new ArrayList<Integer>();
		if(loopList4Label.containsKey(label))
			loopList = loopList4Label.get(label);
		
		//System.out.println("PostWordList;"+wordList);
		//System.out.println("outputEdgeList;"+outputEdgeList);
		//System.out.println("inputEdgesofNextNodeList;"+inputEdgesofNextNodeList);
		
		if(postOpenidSet.contains(id))
			postOpenidSet.remove(id);
		
		int edgeIndex;
		

		
		
		
		if(outputEdgeList.size()==0){
			for(String word:label.split(" "))
				if(gcp.OutputEdges4KeyWord.containsKey(word))
					outputEdgeList.addAll(gcp.OutputEdges4KeyWord.get(word));
		}
			
		if(inputEdgesofNextNodeList.size()==0){
			for(String word:label.split(" "))
				if(gcp.InputEdgesofNextNode4KeyWord.containsKey(word))
					inputEdgesofNextNodeList.addAll(gcp.InputEdgesofNextNode4KeyWord.get(word));
		}
		/*if(loopList.size()==0){
			for(String word:label.split(" "))
				if(gcp.LoopEdges4KeyWord.containsKey(word))
					loopList.addAll(gcp.LoopEdges4KeyWord.get(word));
		}*/		
		
		if(outputEdgeList.size()+inputEdgesofNextNodeList.size()+loopList.size()==0) return;
		edgeIndex = r.nextInt(outputEdgeList.size()+inputEdgesofNextNodeList.size()+loopList.size());
		int edge;
		if(edgeIndex<outputEdgeList.size()){
			edge = outputEdgeList.get(edgeIndex);
			int count = 0;
			Set<Integer> Nodes = g.getPredecessorsOfVertex(id);
			while(edge==1&&Nodes.size()==1&&!containsGateway(g, Nodes)){
			//while(edge==1&&g.getPredecessorsOfVertex(id).size()==1){
				edge = outputEdgeList.get(edgeIndex);
				count++;
				if(count>10)
					edge++;
					
			}
			if(edge>1){
				extendCurrentPostPattern(r, gcp, sf,g, id, postOpenidSet, edge, wordList);
				
				
			}
			else if(edge==1){
				if(gcp.SeqLength.isEmpty())
					gcp.SeqLength.addAll(tempgcp.SeqLength);
				int length = gcp.SeqLength.get(r.nextInt(gcp.SeqLength.size()));
				//int length = r.nextInt(gcp.SeqLength.size());
				List<String> wordList1 = new ArrayList<String>(wordList);
				for(int i=0;i<length;i++){
					List<Integer> ids = extendPostNodes(r, gcp, sf, g, id, edge, wordList1);
					for(Integer tid:ids){
						id = tid;
					}
					
					wordList1 = new ArrayList<String>();
					label = g.getLabel(id);
					for(String word:label.split(" ")){
						if(gcp.KeyWordsInPostLabel.containsKey(word))
							wordList1.addAll(gcp.KeyWordsInPostLabel.get(word));
					}
					
				}
				
				postOpenidSet.add(id);
			}
			//extendCurrentPostStructure(r, sf, g, id, postOpenidSet, edge, wordList);
		}
		else if(edgeIndex<outputEdgeList.size()+inputEdgesofNextNodeList.size()){
			edge = inputEdgesofNextNodeList.get(edgeIndex-outputEdgeList.size());
			if(edge>1)
				extendCurrentPrePattern4NextNode(r, gcp, sf,g, id, preOpenidSet, postOpenidSet, edge, wordList);
			else if(edge==1){
				List<Integer> ids = extendPostNodes(r, gcp, sf, g, id, edge, wordList);
				postOpenidSet.addAll(ids);
			}
		}	
		else if(!loopOpenidSet.contains(id)){//loop
			loopList.get(edgeIndex-(outputEdgeList.size()+inputEdgesofNextNodeList.size()));
			loopOpenidSet.add(id);
			postOpenidSet.add(id);
		}
	}
	
	private void extendPreStructure(Random r, GraphCollectionProperty gcp,
			SyntheticFragment sf, SimpleGraph g, Integer id, List<Integer> preOpenidSet,
			List<Integer> postOpenidSet, Set<Integer> loopOpenidSet) {
		String label = g.getLabel(id);
		//System.out.println(id+";pre;"+label);
		/*List<String> wordList = new ArrayList<String>();
		List<Integer> inputEdgeList = new ArrayList<Integer>();
		List<Integer> outputEdgesofPreNodeList = new ArrayList<Integer>();
		List<Integer> loopList = new ArrayList<Integer>();
		for(String word:label.split(" ")){
			if(gcp.StopWordSet.contains(word))
				continue;
			if(gcp.KeyWordsInPreLabel.containsKey(word))
				wordList.addAll(gcp.KeyWordsInPreLabel.get(word));
			if(gcp.InputEdges4KeyWord.containsKey(word))
				inputEdgeList.addAll(gcp.InputEdges4KeyWord.get(word));
			//if(gcp.OutputEdgesofPreviousNode4KeyWord.containsKey(word))
				//outputEdgesofPreNodeList.addAll(gcp.OutputEdgesofPreviousNode4KeyWord.get(word));
			if(gcp.LoopEdges4KeyWord.containsKey(word))
				loopList.addAll(gcp.LoopEdges4KeyWord.get(word));
		}*/
		List<String> wordList = new ArrayList<String>();
		if(preWordList4Label.containsKey(label))
			wordList = preWordList4Label.get(label);
		List<Integer> inputEdgeList = new ArrayList<Integer>();
		if(inputEdgeList4Label.containsKey(label))
			inputEdgeList = inputEdgeList4Label.get(label);
		List<Integer> outputEdgesofPreNodeList = new ArrayList<Integer>();
		if(outputEdgesofPreviousNodeList4Label.containsKey(label))
			outputEdgesofPreNodeList = outputEdgesofPreviousNodeList4Label.get(label);
		List<Integer> loopList = new ArrayList<Integer>();
		if(loopList4Label.containsKey(label))
			loopList = loopList4Label.get(label);
		
		
		
		
		if(preOpenidSet.contains(id))
			preOpenidSet.remove(id);
		//
		if(inputEdgeList.isEmpty()&&inputEdgeList.size()==0){
			for(String word:label.split(" "))
				if(tempgcp.InputEdges4KeyWord.containsKey(word))
					inputEdgeList.addAll(tempgcp.InputEdges4KeyWord.get(word));
		}
		if(outputEdgesofPreNodeList.isEmpty()&&outputEdgesofPreNodeList.size()==0){
			for(String word:label.split(" "))
				if(tempgcp.OutputEdgesofPreviousNode4KeyWord.containsKey(word))
					outputEdgesofPreNodeList.addAll(tempgcp.OutputEdgesofPreviousNode4KeyWord.get(word));
		}	
		if(loopList.isEmpty()&&loopList.size()==0){
			for(String word:label.split(" "))
				if(gcp.LoopEdges4KeyWord.containsKey(word))
					loopList.addAll(gcp.LoopEdges4KeyWord.get(word));
		}
		
		if(inputEdgeList.size()+outputEdgesofPreNodeList.size()+loopList.size()==0)return;
		int edgeIndex = r.nextInt(inputEdgeList.size()+outputEdgesofPreNodeList.size()+loopList.size());
		int edge;
		if(edgeIndex<inputEdgeList.size()){
			
			edge = inputEdgeList.get(edgeIndex);
			int count = 0;
			Set<Integer> Nodes = g.getSuccessorsOfVertex(id);
			while(edge==1&&Nodes.size()==1&&!containsGateway(g, Nodes)){
				edge = inputEdgeList.get(edgeIndex);
				count++;
				if(count>10) edge++;
			}
			if(edge>1){
				extendCurrentPrePattern(r, gcp, sf,g, id, preOpenidSet, edge, wordList);
				//System.out.println(";;;;;;extendCurrentPrePattern, join"+edge);
			}else if(edge==1){
				//List<Integer> ids = extendPreNodes(r, sf, g, id, edge, wordList);
				//preOpenidSet.addAll(ids);
				
				if(gcp.SeqLength.isEmpty())
					gcp.SeqLength.addAll(tempgcp.SeqLength);
				int length = gcp.SeqLength.get(r.nextInt(gcp.SeqLength.size()));
				//int length = r.nextInt(gcp.SeqLength.size());
				
				//System.out.println(";;;;;;extendCurrentPreNodes, sequence"+length);
				List<String> wordList1 = new ArrayList<String>(wordList);
				for(int i=0;i<length;i++){
					List<Integer> ids = extendPreNodes(r, gcp, sf, g, id, edge, wordList1);
					for(Integer tid:ids){
						id = tid;
					}
					
					label = g.getLabel(id);
					wordList1 = new ArrayList<String>();
					for(String word:label.split(" ")){
						if(gcp.KeyWordsInPreLabel.containsKey(word))
							wordList1.addAll(gcp.KeyWordsInPreLabel.get(word));
					}
					
				}
				
				preOpenidSet.add(id);
				
				
			}
			//System.out.println("extendpre");
			//extendCurrentPreStructure(r, sf, g, id, preOpenidSet, edge, wordList);
		}
		else if(edgeIndex<inputEdgeList.size()+outputEdgesofPreNodeList.size()){
			
			edge = outputEdgesofPreNodeList.get(edgeIndex-inputEdgeList.size());
			if(edge>1){
				extendCurrentPostPattern4PreNode(r, gcp, sf,g, id, preOpenidSet, postOpenidSet, edge, wordList);
				//System.out.println(";;;;;;extendCurrentPostPattern4PreNode, split"+edge);
				
			}
			else if(edge==1){
				List<Integer> ids = extendPreNodes(r, gcp, sf, g, id, edge, wordList);
				preOpenidSet.addAll(ids);
				//System.out.println(";;;;;;extendCurrentPreNodes, sequence"+"+1+");
			}
		}
		else if(!loopOpenidSet.contains(id)){
			loopList.get(edgeIndex-(inputEdgeList.size()+outputEdgesofPreNodeList.size()));
			loopOpenidSet.add(id);
			preOpenidSet.add(id);
			//System.out.println("extendloop");
		}
	}
	private boolean containsGateway(SimpleGraph docGraph,
			Set<Integer> Set) {
		
	
		for(Integer i: Set){
			String nodeType = docGraph.getAttributeValue(i, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			if(nodeType!=null&&(nodeType.contains("Gateway")||nodeType.contains("xor")||nodeType.contains("or")||nodeType.contains("and"))){ //gateways, label like "AND" and "XOR"
				return true;
			}
		}
	
		return false;
	}

	private void extendCurrentPreStructure(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, Set<Integer> preOpenidSet, int edge,
			List<String> wordList) {
		if(preOpenidSet.contains(id))
			preOpenidSet.remove(id);
		if(edge>0&&!wordList.isEmpty()){
			for(int i=0; i<edge; i++){
				if(wordList.isEmpty()) break;
				String word = wordList.get(r.nextInt(wordList.size()));
				if(!sf.KeyWord2Label.containsKey(word)){
					wordList.remove(word);
					i--;
					continue;
				}
				else{
					List<String> tempStringList = sf.KeyWord2Label.get(word);
					String label = tempStringList.get(r.nextInt(tempStringList.size()));
					int newid = g.addVertex(label);
					
					List<String> tempNodeTypeList = gcp.NodeTypeInPreNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE));
					//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
					String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.setLabel(newid, type);
					
					g.addEdge(newid, id);
					//if(preOpenidSet.size()<buffersize)
					preOpenidSet.add(newid);
				}
			}
		}
		//System.out.println("extendCurrentPreStructure");
	}

	private void extendCurrentPostStructure(Random r, GraphCollectionProperty gcp,
			SyntheticFragment sf, SimpleGraph g, Integer id,
			Set<Integer> postOpenidSet, int edge, List<String> wordList) {
		
		if(postOpenidSet.contains(id))
			postOpenidSet.remove(id);
		if(edge>0&&!wordList.isEmpty()){
			for(int i=0; i<edge; i++){
				if(wordList.isEmpty()) break;
				String word = wordList.get(r.nextInt(wordList.size()));
				if(!sf.KeyWord2Label.containsKey(word)){
					wordList.remove(word);
					i--;
					continue;
				}
				else{
					List<String> tempStringList = sf.KeyWord2Label.get(word);
					String label = tempStringList.get(r.nextInt(tempStringList.size()));
					int newid = g.addVertex(label);
					List<String> tempNodeTypeList = gcp.NodeTypeInPostNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE));
					//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
					String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.setLabel(newid, type);
					
					g.addEdge(id, newid);
					//if(postOpenidSet.size()<buffersize)
					postOpenidSet.add(newid);
				}
			}
		}
		//System.out.println("extendCurrentPostStructure");
	}
	
	private void extendCurrentPostPattern(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, List<Integer> postOpenidSet, int edge,
			List<String> wordList) {
		
		if(postOpenidSet.contains(id))
			postOpenidSet.remove(id);
		if(gcp.SplitPatterns.containsKey(edge)){
			List<Pattern4MultiNodes> patternList = gcp.SplitPatterns.get(edge);
			if(patternList.isEmpty()) {
				//gcp.SplitPatterns.get(edge);
				if(tempgcp.SplitPatterns.containsKey(edge)&&tempgcp.SplitPatterns.get(edge).size()>0)
					patternList.addAll(tempgcp.SplitPatterns.get(edge));
				else{
					postOpenidSet.add(id);
					return;
				}
				
			}
			//System.out.println("splitpatternlistsize"+patternList.size());
			Pattern4MultiNodes pattern = patternList.get(r.nextInt(patternList.size()));
			String type = pattern.gateway;
			if(!type.contains("Gateway")){
				pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
				type = pattern.gateway;
			}
			int	newid = g.addVertex(pattern.gateway);
			/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
				gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
			//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
			String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
			g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
			g.setLabel(newid, type);
			
			g.addEdge(id, newid);
			
			
			//id = newid; 
			
			List<Integer> nodeIdList = extendPostNodes(r, gcp, sf, g, newid, edge, wordList);
			//System.out.println(nodeIdList.size());
			//System.out.println("post set of id:"+g.getSuccessorsOfVertex(id));
			//System.out.println("post open list:"+postOpenidSet);
			
			/*if(nodeIdList.size()!=edge){
				postOpenidSet.add(id);
				return;
			}*/
			if(nodeIdList.size()!=pattern.nodeList.size()){
				if(nodeIdList.size()>1&&gcp.SplitPatterns.containsKey(nodeIdList.size())){
					patternList = gcp.SplitPatterns.get(nodeIdList.size());
					if(patternList.isEmpty()) {
						if(tempgcp.SplitPatterns.containsKey(edge)&&tempgcp.SplitPatterns.get(edge).size()>0)
							patternList.addAll(tempgcp.SplitPatterns.get(edge));
						else
							return;
					}
					pattern = patternList.get(r.nextInt(patternList.size()));
					g.setLabel(newid, pattern.gateway);
				}else
				{
					postOpenidSet.addAll(nodeIdList);
					return;
				}
				
			}
			Hashtable<Integer,Integer> idMap = new Hashtable<Integer,Integer>();
			
			if(nodeIdList.size()!=pattern.nodeList.size())
				System.out.println("size not right ------------------------------");
			for(int i=0; i<nodeIdList.size()&&i<pattern.nodeList.size(); i++)
				idMap.put(pattern.nodeList.get(i), nodeIdList.get(i));
			
			Set<Integer> tempPostOpenidSet = new HashSet<Integer>();
			
			Hashtable<HashSet<Integer>,Integer> nodes2connection = new Hashtable<HashSet<Integer>,Integer>();
			//if(pattern.nodeList.size()>18)
				//System.out.println("pattern size not right ------------------------------"+pattern.nodeList);
			extendPostBasicConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPostOpenidSet);
			extendPostTransitiveConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPostOpenidSet);
			
			postOpenidSet.addAll(tempPostOpenidSet);
			for(int i: nodeIdList){
				Set<Integer> temp = new HashSet<Integer>();
				temp.addAll(g.getSuccessorsOfVertex(i));
				if(temp.size()>1){
					int j = g.addVertex(type);
					g.setAttributeValue(j, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.addEdge(i, j);
					for(int k : temp){
						g.removeEdge(i, k);
						g.addEdge(j, k);
					}
				}
			}
			
		}else{
			postOpenidSet.add(id);
		}
		
		
		
	}
	
	
	private void extendCurrentPrePattern(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, List<Integer> preOpenidSet, int edge,
			List<String> wordList) {
		
		if(preOpenidSet.contains(id))
			preOpenidSet.remove(id);
		
		if(gcp.JoinPatterns.containsKey(edge)){
			List<Pattern4MultiNodes> patternList = gcp.JoinPatterns.get(edge);
			//System.out.println("joinpatternlistsize"+patternList.size());
			if(patternList.isEmpty()) {
				
				if(tempgcp.SplitPatterns.containsKey(edge)&&tempgcp.JoinPatterns.get(edge).size()>0)
					patternList.addAll(tempgcp.JoinPatterns.get(edge));
				else{
					preOpenidSet.add(id);
					return;
				}
				
				
			}
			Pattern4MultiNodes pattern = patternList.get(r.nextInt(patternList.size()));
			if(patternList.isEmpty()) {
				preOpenidSet.add(id);
				return;
			}
			String type = pattern.gateway;
			if(!type.contains("Gateway")){
				pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
				type = pattern.gateway;
			}
			
			int newid = g.addVertex(pattern.gateway);
			/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
				gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
			//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
			String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
			g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
			g.setLabel(newid, type);
			g.addEdge(newid, id);
			//id = newid; 
			
			List<Integer> nodeIdList = extendPreNodes(r, gcp, sf, g, newid, edge, wordList);
			/*if(nodeIdList.size()!=(edge)){
				preOpenidSet.add(id);
				return;
			}*/
			if(nodeIdList.size()!=pattern.nodeList.size()){
				if(nodeIdList.size()>1&&gcp.JoinPatterns.containsKey(nodeIdList.size())){
					patternList = gcp.JoinPatterns.get(nodeIdList.size());
					if(patternList.isEmpty()) {
						if(tempgcp.SplitPatterns.containsKey(edge)&&tempgcp.JoinPatterns.get(edge).size()>0)
							patternList.addAll(tempgcp.JoinPatterns.get(edge));
						else{
							
							return;
						}
						
					}
					pattern = patternList.get(r.nextInt(patternList.size()));
					g.setLabel(newid, pattern.gateway);
				}
				else 
				{
					preOpenidSet.addAll(nodeIdList);
					return;
				}	
			}
			Hashtable<Integer,Integer> idMap = new Hashtable<Integer,Integer>();
			
			if(nodeIdList.size()!=pattern.nodeList.size())
				System.out.println("size not right ------------------------------");
			
			for(int i=0; i<nodeIdList.size(); i++)
				idMap.put(pattern.nodeList.get(i), nodeIdList.get(i));
			
			Set<Integer> tempPreOpenidSet = new HashSet<Integer>();
			
			Hashtable<HashSet<Integer>,Integer> nodes2connection = new Hashtable<HashSet<Integer>,Integer>();
			
			extendPreBasicConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPreOpenidSet);
			extendPreTransitiveConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPreOpenidSet);

			preOpenidSet.addAll(tempPreOpenidSet);
			
			for(int i: nodeIdList){
				Set<Integer> temp = new HashSet<Integer>();
				temp.addAll(g.getPredecessorsOfVertex(i));
				if(temp.size()>1){
					int j = g.addVertex(type);
					g.setAttributeValue(j, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.addEdge(j, i);
					for(int k : temp){
						g.removeEdge(k,i);
						g.addEdge(k,j);
					}
				}
			}
			
		}else{
			preOpenidSet.add(id);
		}
		
		
		
	}
	
	private void extendCurrentPrePattern4NextNode(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, List<Integer> preOpenidSet, List<Integer> postOpenidSet, int edge,
			List<String> wordList) {
		
		int preid = -1;
		for(Integer pre:g.getPredecessorsOfVertex(id))
			preid = pre;
		
		if(postOpenidSet.contains(id))
			postOpenidSet.remove(id);
		
		
		if(tempgcp.nonterminativeJoinPatterns.containsKey(edge)&&!tempgcp.nonterminativeJoinPatterns.get(edge).isEmpty()){
			
			List<Pattern4MultiNodes> patternList = gcp.nonterminativeJoinPatterns.get(edge);
			if(patternList.isEmpty())
				patternList.addAll(tempgcp.nonterminativeJoinPatterns.get(edge));
			//System.out.println("start extendCurrentPrePattern4NextNode");
			Pattern4MultiNodes pattern = patternList.get(r.nextInt(patternList.size()));
			
			
			//int newid = g.addVertex(pattern.gateway);
			//g.addEdge(newid, id);
			//id = newid; 
			String type = pattern.gateway;
			if(!type.contains("Gateway")){
				pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
				type = pattern.gateway;
			}
			int joinNode = g.addVertex(pattern.gateway);
			/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
				gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
			//g.setAttributeValue(joinNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
			String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
			g.setAttributeValue(joinNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
			
			g.setLabel(joinNode, type);
			int newid = -1;
			List<Integer> nodeIdList = extendPostNodes(r, gcp, sf, g, joinNode, 1, wordList);
			if(!nodeIdList.isEmpty()){
				for(Integer i : nodeIdList)
					newid = i;
				postOpenidSet.add(newid);
				g.addEdge(joinNode, newid);
			}
			
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			
			
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			String label = g.getLabel(id);
			wordList = preWordList4Label.get(label);
			nodeIdList = extendPreNodes(r, gcp, sf, g, joinNode, edge-1, wordList);	
			if(nodeIdList.size()!=(edge-1)){
				postOpenidSet.add(id);
				return;
			}
			nodeIdList.add(id);
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			g.addEdge(id, joinNode);
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			//System.out.print("a");
			if(nodeIdList.size()!=pattern.nodeList.size()&&gcp.nonterminativeJoinPatterns.containsKey(nodeIdList.size())){
				if(nodeIdList.size()>1){
					patternList = gcp.nonterminativeJoinPatterns.get(nodeIdList.size());
					if(patternList.isEmpty()) {
						patternList.addAll(tempgcp.nonterminativeJoinPatterns.get(edge));
					}
					pattern = patternList.get(r.nextInt(patternList.size()));
					g.setLabel(joinNode, pattern.gateway);
				}else 
				{
					System.out.print("return");
					//preOpenidSet.addAll(nodeIdList);
					return;
				}
				/*for(Integer join:nodeIdList){
					g.addEdge(join, joinNode);
				}*/
			}
			//System.out.print("b");
			Hashtable<Integer,Integer> idMap = new Hashtable<Integer,Integer>();
			int idm;
			
			List<Integer> tempList = new ArrayList<Integer>();
			tempList.addAll(pattern.nodeList);
			tempList.removeAll(pattern.terminativeNodes);
			idm = tempList.get(r.nextInt(tempList.size()));
			idMap.put(idm, id);
			
			for(int i=0,j=0; i<nodeIdList.size()&&j<pattern.nodeList.size(); i++, j++){
				if(nodeIdList.get(i)== id) i++;
				if(pattern.nodeList.get(j) == idm) j++;
				if(i<nodeIdList.size()&&j<pattern.nodeList.size())
					idMap.put(pattern.nodeList.get(j), nodeIdList.get(i));
			}
			
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			//System.out.print("c");
			Set<Integer> tempPreOpenidSet = new HashSet<Integer>();
			
			Hashtable<HashSet<Integer>,Integer> nodes2connection = new Hashtable<HashSet<Integer>,Integer>();
			
			extendPreBasicConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPreOpenidSet);
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.print("wrong!!");else System.out.print("right!!");
			extendPreTransitiveConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPreOpenidSet);
			
			
			//if(g.getPredecessorsOfVertex(id).contains(joinNode))System.out.println("wrong!!");else System.out.println("right!!");
			//if(g.getPredecessorsOfVertex(id).contains(joinNode)) g.removeEdge(joinNode,id);
			if(g.getPredecessorsOfVertex(id).size()>1){
				if(preid!=-1)
					g.removeEdge(preid, id);
				Set<Integer> connections = new HashSet<Integer>();
				for(Integer connection: tempPreOpenidSet){
					if(g.existPath(connection, id)){
						connections.add(connection);
					}
				}
				//getConnectionNodes4preNode(g, id, connections);
				if(connections.size()>1){
					int splitNode = g.addVertex(pattern.gateway);
					/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
						gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
					//g.setAttributeValue(splitNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
					type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
					g.setAttributeValue(splitNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.setLabel(splitNode, type);
					for(Integer openid: connections){
						g.addEdge(splitNode, openid);
					}
					if(preid!=-1)
						g.addEdge(preid, splitNode);
				}else{
					for(Integer openid: connections){
						if(preid!=-1)
						g.addEdge(preid, openid);
					}
				}
				tempPreOpenidSet.removeAll(connections);
			}
			
			preOpenidSet.addAll(tempPreOpenidSet);
		}else{
			postOpenidSet.add(id);
		}
		//System.out.println("end extendCurrentPrePattern4NextNode");
	}
	

	

	private void extendCurrentPostPattern4PreNode(Random r,
			GraphCollectionProperty gcp, SyntheticFragment sf, SimpleGraph g,
			Integer id, List<Integer> preOpenidSet, List<Integer> postOpenidSet,
			int edge, List<String> wordList) {
		// TODO Auto-generated method stub
		
		//System.out.println("start extendCurrentPostPattern4PreNode");
		int postid = -1;
		for(Integer post:g.getSuccessorsOfVertex(id))
			postid = post;
		
		if(preOpenidSet.contains(id))
			preOpenidSet.remove(id);
		if(tempgcp.nonterminativeSplitPatterns.containsKey(edge)&&!tempgcp.nonterminativeSplitPatterns.get(edge).isEmpty()){
			
			
			
			List<Pattern4MultiNodes> patternList = gcp.nonterminativeSplitPatterns.get(edge);
			//System.out.println("joinpatternlistsize"+patternList.size());
			if(patternList.isEmpty())
				patternList.addAll(tempgcp.nonterminativeSplitPatterns.get(edge));
			Pattern4MultiNodes pattern;
				pattern = patternList.get(r.nextInt(patternList.size()));
			
				
			
			
			//int newid = g.addVertex(pattern.gateway);
			//g.addEdge(newid, id);
			//id = newid; 
			String type = pattern.gateway;
			if(!type.contains("Gateway")){
				pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
				type = pattern.gateway;
			}
			int splitNode = g.addVertex(pattern.gateway);
			/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
				gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
			//g.setAttributeValue(splitNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
			String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
			g.setAttributeValue(splitNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
			g.setLabel(splitNode, type);
			int newid = -1;
			List<Integer> nodeIdList = extendPreNodes(r, gcp, sf, g, splitNode, 1, wordList);
			if(!nodeIdList.isEmpty()){
				for(Integer i : nodeIdList)
					newid = i;
			
					preOpenidSet.add(newid);
			
					g.addEdge(newid, splitNode);
			}
			
			String label = g.getLabel(id);
			wordList = postWordList4Label.get(label);
			//wordList = getWordList4PostNodes(r, gcp, sf, g, newid);
			nodeIdList = extendPostNodes(r, gcp, sf, g, splitNode, edge-1, wordList);	
			if(nodeIdList.size()!=(edge-1)){
				preOpenidSet.add(id);
				return;
			}
			nodeIdList.add(id);
			g.addEdge(splitNode, id);
			if(nodeIdList.size()!=pattern.nodeList.size()){
				if(nodeIdList.size()>1&&gcp.nonterminativeSplitPatterns.containsKey(nodeIdList.size())){
					patternList = gcp.nonterminativeSplitPatterns.get(nodeIdList.size());
					if(patternList.isEmpty()) {
						patternList.addAll(tempgcp.nonterminativeSplitPatterns.get(edge));
					}
					pattern = patternList.get(r.nextInt(patternList.size()));
					g.setLabel(splitNode, pattern.gateway);
				}else 
				{
					//postOpenidSet.addAll(nodeIdList);
					return;
				}
				/*for(Integer split:nodeIdList){
					g.addEdge(splitNode, split);
				}*/
			}
			
			Hashtable<Integer,Integer> idMap = new Hashtable<Integer,Integer>();
			int idm;
			List<Integer> tempList = new ArrayList<Integer>();
			tempList.addAll(pattern.nodeList);
			tempList.removeAll(pattern.terminativeNodes);
			idm = tempList.get(r.nextInt(tempList.size()));
			idMap.put(idm,id);
			for(int i=0,j=0; i<nodeIdList.size()&&j<pattern.nodeList.size(); i++, j++){
				if(nodeIdList.get(i)== id) i++;
				if(pattern.nodeList.get(j) == idm) j++;
				if(i<nodeIdList.size()&&j<pattern.nodeList.size())
					idMap.put(pattern.nodeList.get(j), nodeIdList.get(i));
			}

			Set<Integer> tempPostOpenidSet = new HashSet<Integer>();
			
			Hashtable<HashSet<Integer>,Integer> nodes2connection = new Hashtable<HashSet<Integer>,Integer>();
			
			extendPostBasicConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPostOpenidSet);
			extendPostTransitiveConnections(r, gcp, sf, g, pattern, idMap, nodes2connection, tempPostOpenidSet);
			
			if(g.getSuccessorsOfVertex(id).size()>1 ){
				if(postid!=-1)
					g.removeEdge(id, postid);
				Set<Integer> connections = new HashSet<Integer>();
				for(Integer connection: tempPostOpenidSet){
					if(g.existPath(id, connection)){
						connections.add(connection);
					}
				}
				//getConnectionNodes4postNode(g, id, connections);
				if(connections.size()>1){
					int joinNode = g.addVertex(pattern.gateway);
					/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
						gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
					//g.setAttributeValue(joinNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
					type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
					g.setAttributeValue(joinNode, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
					g.setLabel(joinNode, type);
					for(Integer openid: connections){
						g.addEdge(openid, joinNode);
					}
					if(postid!=-1)
					g.addEdge(joinNode, postid);
				}else{
					for(Integer openid: connections){
						if(postid!=-1)
						g.addEdge(openid, postid);
					}
				}
				tempPostOpenidSet.removeAll(connections);
			}
			
			preOpenidSet.addAll(tempPostOpenidSet);			
		}else{
			preOpenidSet.add(id);
		}
		
		//System.out.println("end extendCurrentPostPattern4PreNode");
	}
	
	private void getConnectionNodes4preNode(SimpleGraph g, Integer id,
			Set<Integer> connections) {
		// TODO Auto-generated method stub
		//if(g.getPredecessorsOfVertex(id).isEmpty())
			
		for(Integer openid: g.getPredecessorsOfVertex(id)){
			if(g.getPredecessorsOfVertex(openid).isEmpty())
				connections.add(openid);
			else
				getConnectionNodes4preNode(g, openid, connections);
		}
	}
	
	
	private void getConnectionNodes4postNode(SimpleGraph g, Integer id,
			Set<Integer> connections) {
		// TODO Auto-generated method stub
		for(Integer openid: g.getSuccessorsOfVertex(id)){
			if(g.getSuccessorsOfVertex(openid).isEmpty())
				connections.add(openid);
			else
				getConnectionNodes4postNode(g, openid, connections);
		}
		
	}

	private void extendPreTransitiveConnections(Random r,
			GraphCollectionProperty gcp, SyntheticFragment sf, SimpleGraph g,
			Pattern4MultiNodes pattern, Hashtable<Integer, Integer> idMap,
			Hashtable<HashSet<Integer>, Integer> nodes2connection,
			Set<Integer> tempPreOpenidSet) {
		
		if(!pattern.transitiveConnections.isEmpty()){
			Set<HashSet<Integer>> transitiveConnections = pattern.transitiveConnections;
			for(HashSet<Integer> nodes:transitiveConnections){
				HashSet<Integer> visitedNodes = new HashSet<Integer>();
				HashSet<Integer> newNodes = new HashSet<Integer>();
				List<Integer> connection = new ArrayList<Integer>(); 
				List<String>wordList = new ArrayList<String>();
				List<Integer> nodeIdList = new ArrayList<Integer>();
				
				String type = pattern.gateway;
				if(!type.contains("Gateway")){
					pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					type = pattern.gateway;
				}
				int newid = g.addVertex(pattern.gateway);
				/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
					gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
				//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
				String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
				g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				g.setLabel(newid, type);
				for(HashSet<Integer> existedNodes : nodes2connection.keySet()){
					if(nodes.containsAll(existedNodes)){
						visitedNodes.addAll(existedNodes);
						int tempid = nodes2connection.get(existedNodes);
						newNodes.add(tempid);
						
					}
				}
				for(Integer node: nodes){
					int id = idMap.get(node);
					if(!visitedNodes.contains(id))
						newNodes.add(id);
				}
				
				for(Integer id : newNodes){
					
					if(gcp.SeqLength.isEmpty())
						gcp.SeqLength.addAll(tempgcp.SeqLength);
					int length = gcp.SeqLength.get(r.nextInt(gcp.SeqLength.size()));
					if(length>1){
						for(int i=0;i<length-1;i++){
							List<String> tempWordList = getWordList4PreNodes(r, gcp, sf, g, id);
							List<Integer> ids = extendPreNodes(r, gcp, sf, g, id, 1, tempWordList);
							for(Integer tid:ids){
								id = tid;
							}					
						}
					}
					
					
					connection.add(id);
					g.addEdge(newid, id);
					wordList.addAll(getWordList4PreNodes(r, gcp, sf, g, id));
				}
				tempPreOpenidSet.removeAll(newNodes);
				
				nodeIdList = extendPreNodes(r, gcp, sf, g, newid, 1, wordList);
				if(!nodeIdList.isEmpty()){
					for(Integer a : nodeIdList){
						//nodes2connection.put(nodes, a);
						tempPreOpenidSet.add(a);
					}
				}else{
					tempPreOpenidSet.add(newid);
				}
			}
		}
	}
	
	private void extendPostTransitiveConnections(Random r,
			GraphCollectionProperty gcp, SyntheticFragment sf, SimpleGraph g,
			Pattern4MultiNodes pattern, Hashtable<Integer, Integer> idMap,
			Hashtable<HashSet<Integer>, Integer> nodes2connection,
			Set<Integer> tempPostOpenidSet) {
		
		if(!pattern.transitiveConnections.isEmpty()){
			Set<HashSet<Integer>> transitiveConnections = pattern.transitiveConnections;
			
			for(HashSet<Integer> nodes:transitiveConnections){
				HashSet<Integer> visitedNodes = new HashSet<Integer>();
				HashSet<Integer> newNodes = new HashSet<Integer>();
				List<Integer> connection = new ArrayList<Integer>(); 
				List<String>wordList = new ArrayList<String>();
				List<Integer> nodeIdList = new ArrayList<Integer>();
				String type = pattern.gateway;
				if(!type.contains("Gateway")){
					pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					type = pattern.gateway;
				}
				int newid = g.addVertex(pattern.gateway);
				/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
					gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
				//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
				String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
				g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				g.setLabel(newid, type);
				for(HashSet<Integer> existedNodes : nodes2connection.keySet()){
					if(nodes.containsAll(existedNodes)){
						visitedNodes.addAll(existedNodes);
						int tempid = nodes2connection.get(existedNodes);
						newNodes.add(tempid);
						
					}
				}
				for(Integer node: nodes){
					int id = idMap.get(node);
					if(!visitedNodes.contains(id))
						newNodes.add(id);
				}
				//newNodes.addAll(nodes);
				//newNodes.removeAll(visitedNodes);
				
				for(Integer nid : newNodes){//add sequences
					if(gcp.SeqLength.isEmpty())
						gcp.SeqLength.addAll(tempgcp.SeqLength);
					int length = gcp.SeqLength.get(r.nextInt(gcp.SeqLength.size()));
					int id = nid;
					if(length>1){
						for(int i=0;i<length-1;i++){
							List<String> tempWordList = getWordList4PostNodes(r, gcp, sf, g, id);
							List<Integer> ids = extendPostNodes(r, gcp, sf, g, id, 1, tempWordList);
							if(!ids.isEmpty()){
								for(Integer tid:ids){
									id = tid;
								}
							}
												
						}
					}
					
					
					
					//System.out.println(g.getLabel(newid));
					//System.out.println(g.getLabel(node));
					connection.add(id);
					g.addEdge(id, newid);
					wordList.addAll(getWordList4PostNodes(r, gcp, sf, g, id));
				}
				tempPostOpenidSet.remove(newNodes);
				
				nodeIdList = extendPostNodes(r, gcp, sf, g, newid, 1, wordList);
				if(!nodeIdList.isEmpty()){
					for(Integer a : nodeIdList){
						//nodes2connection.put(nodes, a);
						tempPostOpenidSet.add(a);
					}
				}else{
					tempPostOpenidSet.add(newid);
				}
				
			}
		}
	}

	private void extendPreBasicConnections(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Pattern4MultiNodes pattern,
			Hashtable<Integer, Integer> idMap, Hashtable<HashSet<Integer>,Integer> nodes2connection, Set<Integer> tempPreOpenidSet) {
		
		if(!pattern.basicConnections.isEmpty()){
			Set<HashSet<Integer>> basicConnections = pattern.basicConnections;
			for(HashSet<Integer> nodes:basicConnections){
				HashSet<Integer> connection = new HashSet<Integer>(); 
				List<String> wordList = new ArrayList<String>();
				String type = pattern.gateway;
				if(!type.contains("Gateway")){
					pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					type = pattern.gateway;
				}
				int newid = g.addVertex(pattern.gateway);
				/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
					gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
				//g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size())));
				String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
				g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				g.setLabel(newid, type);
				for(Integer node : nodes){
					if(idMap.containsKey(node)){
						int id = idMap.get(node);
						connection.add(id);
						g.addEdge(newid, id);
						wordList.addAll(getWordList4PreNodes(r, gcp, sf, g, id));
					}
				}
				List<Integer> nodeIdList = extendPreNodes(r, gcp, sf, g, newid, 1, wordList);
				
				if(!nodeIdList.isEmpty()){
					for(Integer a : nodeIdList){
						nodes2connection.put(connection, a);
						tempPreOpenidSet.add(a);
					}
				}else{
					nodes2connection.put(connection, newid);
					tempPreOpenidSet.add(newid);
				}
				
			}
		}
	}
	
	private void extendPostBasicConnections(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Pattern4MultiNodes pattern,
			Hashtable<Integer, Integer> idMap, Hashtable<HashSet<Integer>,Integer> nodes2connection, Set<Integer> tempPostOpenidSet) {
		
		if(!pattern.basicConnections.isEmpty()){
			Set<HashSet<Integer>> basicConnections = pattern.basicConnections;
			for(HashSet<Integer> nodes:basicConnections){
				HashSet<Integer> connection = new HashSet<Integer>(); 
				List<String> wordList = new ArrayList<String>();
				List<Integer> nodeIdList = new ArrayList<Integer>();
				String type = pattern.gateway;
				if(!type.contains("Gateway")){
					pattern.gateway = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));
					type = pattern.gateway;
				}
				int newid = g.addVertex(pattern.gateway);
				/*if(gcp.GatewayTypeList==null||gcp.GatewayTypeList.size()==0)
					gcp.GatewayTypeList = new ArrayList<String>(tempgcp.GatewayTypeList);
				String type = gcp.GatewayTypeList.get(r.nextInt(gcp.GatewayTypeList.size()));*/
				g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				g.setLabel(newid, type);
				for(Integer node : nodes){
					if(idMap.containsKey(node)){
						int id = idMap.get(node);
						connection.add(id);
						g.addEdge(id, newid);
						wordList.addAll(getWordList4PostNodes(r, gcp, sf, g, id));
					}
					else
						System.out.println("error!!!");
					
				}
				nodeIdList = extendPostNodes(r, gcp, sf, g, newid, 1, wordList);	
				
				
				if(!nodeIdList.isEmpty()){
					for(Integer a : nodeIdList){
						nodes2connection.put(connection, a);
						tempPostOpenidSet.add(a);
					}
				}else{
					nodes2connection.put(connection, newid);
					tempPostOpenidSet.add(newid);
				}
				
			}
		}
	}


	List<String> getWordList4PreNodes(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id)
	{
		List<String> wordList = new ArrayList<String>();
		String label = g.getLabel(id);
		for(String word:label.split(" ")){
			if(gcp.StopWordSet.contains(word))
				continue;
			if(gcp.KeyWordsInPreLabel.containsKey(word))
				wordList.addAll(gcp.KeyWordsInPreLabel.get(word));
		}
		return wordList;
	}
	
	List<String> getWordList4PostNodes(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id)
	{
		List<String> wordList = new ArrayList<String>();
		String label = g.getLabel(id);
		for(String word:label.split(" ")){
			if(gcp.StopWordSet.contains(word))
				continue;
			if(gcp.KeyWordsInPostLabel.containsKey(word))
				wordList.addAll(gcp.KeyWordsInPostLabel.get(word));
		}
		return wordList;
	}
	
	List<Integer> extendPreNodes(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, int edge,
			List<String> wordList){
		List<Integer> nodeIdList = new ArrayList<Integer>(); 
		for(int i=0; i<edge; i++){
			if(wordList.isEmpty()) break;
			String word = wordList.get(r.nextInt(wordList.size()));
			
			if(!sf.KeyWord2Label.containsKey(word)){
				wordList.remove(word);
				i--;
				continue;
			}
			else{
				
				List<String> tempStringList = new ArrayList<String>(sf.KeyWord2Label.get(word));
				if(!tempStringList.isEmpty()){
					String label = tempStringList.get(r.nextInt(tempStringList.size()));
				int newid = g.addVertex(label);
				List<String> tempNodeTypeList = gcp.NodeTypeInPreNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE));
				if(tempNodeTypeList!=null&&tempNodeTypeList.size()!=0)
					g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
				else
				{
					if(tempgcp.NodeTypeInPreNodes.containsKey(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE)))
						tempNodeTypeList = new ArrayList<String>(tempgcp.NodeTypeInPreNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE)));
					if(tempNodeTypeList!=null&&tempNodeTypeList.size()!=0)
						g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
					else
						g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.IntermediateNodeTypeList.get(r.nextInt(gcp.IntermediateNodeTypeList.size())));
					
				}
					
				g.addEdge(newid, id);
				nodeIdList.add(newid);
				}
				
				//if(preOpenidSet.size()<buffersize)
				//preOpenidSet.add(newid);
			}
		}
		return nodeIdList;
	}
	
	List<Integer> extendPostNodes(Random r, GraphCollectionProperty gcp, SyntheticFragment sf,
			SimpleGraph g, Integer id, int edge,
			List<String> wordList){
		List<Integer> nodeIdList = new ArrayList<Integer>(); 
		for(int i=0; i<edge; i++){
			if(wordList.isEmpty()) break;
			String word = wordList.get(r.nextInt(wordList.size()));
			if(!sf.KeyWord2Label.containsKey(word)){
				wordList.remove(word);
				i--;
				continue;
			}
			else{
				List<String> tempStringList = new ArrayList<String>(sf.KeyWord2Label.get(word));
				if(!tempStringList.isEmpty()){
					String label = tempStringList.get(r.nextInt(tempStringList.size()));
					int newid = g.addVertex(label);
					List<String> tempNodeTypeList = gcp.NodeTypeInPostNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE));
					if(tempNodeTypeList!=null&&tempNodeTypeList.size()!=0)
						g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
					else
					{
						if(tempgcp.NodeTypeInPostNodes.containsKey(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE)))
							tempNodeTypeList = new ArrayList<String>(tempgcp.NodeTypeInPostNodes.get(g.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE)));
						if(tempNodeTypeList!=null&&tempNodeTypeList.size()!=0)
							g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, tempNodeTypeList.get(r.nextInt(tempNodeTypeList.size())));
						else
							g.setAttributeValue(newid, SimpleGraph.ATTRIBUTE_NODE_TYPE, gcp.IntermediateNodeTypeList.get(r.nextInt(gcp.IntermediateNodeTypeList.size())));
						
					}
					g.addEdge(id, newid);
					nodeIdList.add(newid);
				}
				
				//if(preOpenidSet.size()<buffersize)
				//preOpenidSet.add(newid);
			}
		}
		return nodeIdList;
	}
	
	private void initalLists4Label(GraphCollectionProperty gcp, SyntheticFragment sf){
		
		
		for(String label: sf.LabelSet){
			
			List<String> postWordList = new ArrayList<String>();
			List<Integer> outputEdgeList = new ArrayList<Integer>();
			List<Integer> inputEdgesofNextNodeList = new ArrayList<Integer>();
			List<Integer> loopList = new ArrayList<Integer>();
			List<String> preWordList = new ArrayList<String>();
			List<Integer> inputEdgeList = new ArrayList<Integer>();
			List<Integer> outputEdgesofPreNodeList = new ArrayList<Integer>();
			
			
			
			for(String word:label.split(" ")){
				if(gcp.StopWordSet.contains(word))
					continue;
				if(gcp.KeyWordsInPostLabel.containsKey(word))
					postWordList.addAll(gcp.KeyWordsInPostLabel.get(word));
				if(gcp.OutputEdges4KeyWord.containsKey(word))
					outputEdgeList.addAll(gcp.OutputEdges4KeyWord.get(word));
				if(gcp.InputEdgesofNextNode4KeyWord.containsKey(word))
					inputEdgesofNextNodeList.addAll(gcp.InputEdgesofNextNode4KeyWord.get(word));
				
				if(gcp.KeyWordsInPreLabel.containsKey(word))
					preWordList.addAll(gcp.KeyWordsInPreLabel.get(word));
				if(gcp.InputEdges4KeyWord.containsKey(word))
					inputEdgeList.addAll(gcp.InputEdges4KeyWord.get(word));
				if(gcp.OutputEdgesofPreviousNode4KeyWord.containsKey(word))
					outputEdgesofPreNodeList.addAll(gcp.OutputEdgesofPreviousNode4KeyWord.get(word));
				
				if(gcp.LoopEdges4KeyWord.containsKey(word))
					loopList.addAll(gcp.LoopEdges4KeyWord.get(word));
			}
			
			postWordList4Label.put(label, postWordList);
			//current label, post word List
			outputEdgeList4Label.put(label, outputEdgeList) ;
			inputEdgesofNextNodeList4Label.put(label, inputEdgesofNextNodeList);

			preWordList4Label.put(label, preWordList);
							//current label, post word List
			inputEdgeList4Label.put(label, inputEdgeList);
			outputEdgesofPreviousNodeList4Label.put(label, outputEdgesofPreNodeList);

			loopList4Label.put(label, loopList);
			
			
		}
		
		
		
		
	}
	
	
	private void setType4StartAndEnd(List<SimpleGraph> collection, GraphCollectionProperty gcp){
		
		int size = 0;
		//GraphCollectionProperity gcp = new GraphCollectionProperity();
		Random r = new Random(); 
		for(SimpleGraph g:collection){
			for(Integer i :g.getVertices()){
				if(g.getPredecessorsOfVertex(i)==null||g.getPredecessorsOfVertex(i).size()==0){
					size = gcp.StartNodeTypeList.size();
					String type = gcp.StartNodeTypeList.get(r.nextInt(size));
					g.setAttributeValue(i, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				}
				if(g.getSuccessorsOfVertex(i)==null||g.getSuccessorsOfVertex(i).size()==0){
					size = gcp.EndNodeTypeList.size();
					String type = gcp.EndNodeTypeList.get(r.nextInt(size));
					g.setAttributeValue(i, SimpleGraph.ATTRIBUTE_NODE_TYPE, type);
				}
			}
		}
		
	}
	
	
	
	public static void main(String[] args) throws IOException, JWNLException, CloneNotSupportedException {
		
		/*String prefix = Matches.synPrefix1;
		
		GraphCollectionProperity gcp = new GraphCollectionProperity();
		gcp.getProperties();
		gcp.print();*/
		
		
		String prefix = "/bpmnmodels/";	//BPMN*********
		String syntheticPrefix = "/bpmnmodels/synthetic";//BPMN*********
		//String prefix = "models/BPMN/";
		//String syntheticPrefix = "models/synthetic_604/";    //SAP*********
		boolean flag;
		File dir = new File(syntheticPrefix);
		if(!dir.exists()&&!dir.isDirectory()){
			flag = dir.mkdir();
			if(flag) System.out.println("built dir");
			else {
				System.out.println("fail builing dir");
				
			}
		}
		
		XML xml = new XML();		
		//Excel excel = new Excel();
		//WritableWorkbook wwb = excel.buildExcelFile(syntheticPrefix+"statistics.xls");
		
		//WritableSheet ws = excel.createSheet(wwb, "BPMNstatistics");
		//WritableSheet avg_ws = excel.createSheet(wwb, "avg_BPMNstatistics");
		//ws = wwb.getSheet(arg0);
		//int row = 0; int collum = 0;
		//int avg_row = 0;
		
		List<SimpleGraph> gList = xml.loadBPMNfiles(xml, prefix);//BPMN*********
		
		
		GraphCollectionProperty gcp = new GraphCollectionProperty();
		gcp.getProperties(gList);
		gcp.print();
		
		//writeExcelHead(excel, wwb, ws, gcp);
		
		//row+=1;
		//writeExcelContent4OriginalGraphs(excel, wwb, ws, gcp, gList);
		//row+=gList.size();
		
		
		//collectionSize = gcp.collection_graphNum;
		
		int runs = 10;
		int times = 10;
		
		for(int j=0; j<runs; j++){
			
			
			//boolean flag;
			String tempPath=syntheticPrefix+"/synthetic"+j+"/";
			dir = new File(tempPath);
			if(!dir.exists()&&!dir.isDirectory()){
				flag = dir.mkdir();
				if(flag) System.out.println("built dir");
				else {
					System.out.println("fail builing dir");
					j--;
					continue;
				}
			}
			List<SimpleGraph> collection4all = new ArrayList<SimpleGraph>();
			for(int i=0;i<times;i++){
				//List<SimpleGraph> syntheticGraphs = new ArrayList<SimpleGraph>();
				gcp = new GraphCollectionProperty();
				gcp.getProperties(gList);
				SyntheticFragment sf = new SyntheticFragment();
				sf.generateFragments(gcp);
				
				
				SyntheticGraph sg = new SyntheticGraph();
				sg.initalLists4Label(gcp, sf);
				List<SimpleGraph> collection = new ArrayList<SimpleGraph>();
				collection = sg.generateCollection(gcp,sf, true);//true:consider gateways and events; false:do not consider gateways
				collection4all.addAll(collection);
				sg.setType4StartAndEnd(collection,gcp);
				
				
				
				
				//List<SimpleGraph> graphs = new ArrayList<SimpleGraph>();
				int graphid=0;
				double sum_size_n=0; double sum_size_e=0;
				for(SimpleGraph g : collection){
					sum_size_n+=g.getVertices().size();
					sum_size_e+=g.getEdges().size();
					xml.simpleGraph2XML(g,tempPath,i+"_"+graphid+".bpmn");
					//System.out.println("graph "+graphid+":"+g.toString());
					graphid++;
					//graphs.add(g);
				}
				//syntheticGraphs.addAll(graphs);
				//writeExcelContent4GeneratedGraphs(excel, wwb, ws, gcp, collection, i, j, row);
				//row+=collection.size();
				//System.out.println("mean size="+ sum_size_n/collection.size()+","+sum_size_e/collection.size());
				//System.out.println("syntheticGraphListwithLoop: "+sg.syntheticGraphListwithLoop.size());
				
				
			}
			GraphCollectionProperty newgcp = new GraphCollectionProperty();
			newgcp.getProperties(collection4all);
			newgcp.print();
			
			
		}
		//excel.writeExcel(wwb);
		//excel.closeExcel(wwb);
		
		//get statistics for one graph
		
	}
	




	
	
}
