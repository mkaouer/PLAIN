package adpi.java.jadaptiveinterface.evaluate.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class EvaluateInterfaceMenu implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {

		IWorkbenchPage page=window.getActivePage();
		try {
			if(action.getId().equals("adpi.java.jadaptiveinterface.actions.DefectQuality")) {
				page.showView("adpi.java.jadaptiveinterface.views.Defect_Quality");
			}
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
