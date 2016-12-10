package importClass;

import java.util.ArrayList;

import importClass.HumaniseCamelCase;
import importClass.Stemmer;


public interface TopicFinder {
	
	public void findTopic(Stemmer stemmer, HumaniseCamelCase humaniser, ArrayList<String> stopWords);

}
