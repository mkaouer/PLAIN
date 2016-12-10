package adpi.java.jadaptiveinterface.evaluate.views;

import importClass.ExtractClassCandidateEvaluate;
import importClass.MyClass;
import importClass.VisualizationData;


public class EvaluateVisualizationDataSingleton {
	private static VisualizationData data;
	private static MyClass[] candidates;
	
	
	public static MyClass[] getCandidates() {
		return candidates;
	}

	public static void setCandidates(MyClass[] candidateEvaluateTable) {
		EvaluateVisualizationDataSingleton.candidates = candidateEvaluateTable;
	}

	public static VisualizationData getData() {
		return data;
	}

	public static void setData(VisualizationData data) {
		EvaluateVisualizationDataSingleton.data = data;
	}
}
