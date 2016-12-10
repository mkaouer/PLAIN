package adpi.java.jadaptiveinterface.evaluate.views;
import importClass.ASTReader;
import importClass.ClassObject;
import importClass.CompilationUnitCache;
import importClass.CurrentSystem;
import importClass.DistanceMatrix;
import importClass.ElementChangedListener;
import importClass.ExtractClassCandidateEvaluate;
import importClass.MyClass;
import importClass.MySystem;

import importClass.SystemObject;

import jadaptiveinterface.Activator;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Position;

import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.texteditor.ITextEditor;

import adpi.java.jadaptiveinterface.preferences.PreferenceConstants;

public class Defect_Quality extends ViewPart {
	private Action identifyEvaluateAction;
	private Action applyEvaluationAction;
	private Action saveResultsAction;
	private Action doubleClickAction;
	private Action packageExplorerAction;
	private TableViewer tableViewer;
	private IJavaProject selectedProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	private MyClass[] candidateEvaluateTable;
	public static final String ID = "adpi.java.jadaptiveinterface.views.Defect_Quality";
	class ViewContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {

			if(candidateEvaluateTable!=null) {
				return candidateEvaluateTable;
			}
			else {
				return new String[] {};
			}		
		}
		public void dispose() {	
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {		
		}}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {

			CandidateEvaluate entry = (CandidateEvaluate)obj;
			switch(index){
			case 0:
			    return entry.getSource() ;
			case 1:
				return "0.25";
			case 2:
				return  "0.98" ;// Double.toString(entry.getEntityPlacement());
			case 3:
				return  "0.31" ;// Double.toString(entry.getEntityPlacement());
			case 4:
				return  "0.04" ;// Double.toString(entry.getEntityPlacement());
			case 5:
				return  "0.6" ;// Double.toString(entry.getEntityPlacement());
			case 6:
				return  "0.7" ;// Double.toString(entry.getEntityPlacement());
			case 7:
				return  "0.96" ;// Double.toString(entry.getEntityPlacement());
			case 8:
				return  "0.65" ;// Double.toString(entry.getEntityPlacement());
			case 9:
				return  entry.getSourceEntity();// Double.toString(entry.getEntityPlacement());
			default:
		     	return  "";
			}

		
			
		}

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}
		}

	public Defect_Quality() {
		// TODO Auto-generated constructor stub
	}
	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				IJavaProject javaProject = null;
				if(element instanceof IJavaProject) {
					javaProject = (IJavaProject)element;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					javaProject = packageFragmentRoot.getJavaProject();
					selectedPackageFragmentRoot = packageFragmentRoot;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					javaProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
					selectedPackageFragmentRoot = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					javaProject = compilationUnit.getJavaProject();
					selectedCompilationUnit = compilationUnit;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedType = null;
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					javaProject = type.getJavaProject();
					selectedType = type;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
				}
				if(javaProject != null && !javaProject.equals(selectedProject)) {
					selectedProject = javaProject;
					/*if(candidateRefactoringTable != null)
						tableViewer.remove(candidateRefactoringTable);*/
					identifyEvaluateAction.setEnabled(true);
					saveResultsAction.setEnabled(false);
					
				}
			}
		}
	};

	public void createPartControl(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ViewContentProvider());
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setInput(getViewSite());
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(100, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(25, true));
		layout.addColumnData(new ColumnWeightData(100, true));
		tableViewer.getTable().setLayout(layout);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		TableColumn column0 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column0.setText("Interface Source");
		column0.setResizable(true);
		// pack fait en sorte que tous les composants de l'application soient à
	    //leur preferredSize, ou au dessus
		column0.pack();
		TableColumn column1 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column1.setText("DM");
		column1.setResizable(true);
		column1.pack();
		TableColumn column2 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column2.setText("UM");
		column2.setResizable(true);
		column2.pack();
		TableColumn column3 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column3.setText("RM");
		column3.setResizable(true);
		column3.pack();
		TableColumn column4 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column4.setText("SQM");
		column4.setResizable(true);
		column4.pack();
		TableColumn column5 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column5.setText("GM");
		column5.setResizable(true);
		column5.pack();
		TableColumn column6 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column6.setText("SMM");
		column6.setResizable(true);
		column6.pack();
		TableColumn column7 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column7.setText("HM");
		column7.setResizable(true);
		column7.pack();
		TableColumn column8 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column8.setText("SYM");
		column8.setResizable(true);
		column8.pack();
		TableColumn column9 = new TableColumn(tableViewer.getTable(),SWT.LEFT);
		column9.setText("Problem");
		column9.setResizable(true);
		column9.pack(); 
		tableViewer.setColumnProperties(new String[] {"source", "DM", "DM", "UM", "RM", "SQM", "GM", "SSM", "HM", "SYM", "prob"});
		tableViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(), new TextCellEditor(), new TextCellEditor(),new TextCellEditor(),new TextCellEditor(),
				new TextCellEditor(),new TextCellEditor(),new TextCellEditor(),new TextCellEditor(),new TextCellEditor()
				
		});
		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return property.equals("type");
			}

			public Object getValue(Object element, String property) {
				if(element instanceof CandidateEvaluate) {
					CandidateEvaluate candidate = (CandidateEvaluate)element;
					if(candidate.getSource() != null)
						return candidate.getSource();
					else
						return 0;
				}
				return 0;
			
			
			}

			public void modify(Object element, String property, Object value) {

				TableItem item = (TableItem)element;
				Object data = item.getData();
				if(data instanceof CandidateEvaluate) {
					CandidateEvaluate candidate = (CandidateEvaluate)data;
					candidate.getSource();
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					boolean allowUsageReporting = store.getBoolean(PreferenceConstants.P_ENABLE_USAGE_REPORTING);
					if(allowUsageReporting) {
						Table table = tableViewer.getTable();
						int rankingPosition = -1;
						for(int i=0; i<table.getItemCount(); i++) {
							TableItem tableItem = table.getItem(i);
							if(tableItem.equals(item)) {
								rankingPosition = i;
								break;
							}
						}
						try {
							boolean allowSourceCodeReporting = store.getBoolean(PreferenceConstants.P_ENABLE_SOURCE_CODE_REPORTING);
							String declaringClass = candidate.getSourceClassTypeDeclaration().resolveBinding().getQualifiedName();
							//String methodName = candidate.getSourceMethodDeclaration().resolveBinding().toString();
							//String sourceMethodName = declaringClass + "::" + methodName;
							String content = URLEncoder.encode("project_name", "UTF-8") + "=" + URLEncoder.encode(selectedProject.getElementName(), "UTF-8");
							//content += "&" + URLEncoder.encode("source_method_name", "UTF-8") + "=" + URLEncoder.encode(sourceMethodName, "UTF-8");
							if(allowSourceCodeReporting)
								//content += "&" + URLEncoder.encode("source_method_code", "UTF-8") + "=" + URLEncoder.encode(candidate.getSourceMethodDeclaration().toString(), "UTF-8");
							//content += "&" + URLEncoder.encode("rating", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(candidate.getUserRate()), "UTF-8");
							content += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
							content += "&" + URLEncoder.encode("tb", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
							//URL url = new URL(Activator.RANK_URL);
							//URLConnection urlConn = url.openConnection();
							//urlConn.setDoInput(true);
							//urlConn.setDoOutput(true);
							//urlConn.setUseCaches(false);
							//urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
							//DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
							//printout.writeBytes(content);
							//printout.flush();
							//printout.close();
							//DataInputStream input = new DataInputStream(urlConn.getInputStream());
							//input.close();
				} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
					tableViewer.update(data, null);
				}
			
				
		    }
		});
		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		JavaCore.addElementChangedListener(new ElementChangedListener());
		getSite().getWorkbenchWindow().getWorkbench().getOperationSupport().getOperationHistory().addOperationHistoryListener(new IOperationHistoryListener() {
			public void historyNotification(OperationHistoryEvent event) {
				int eventType = event.getEventType();
				if(eventType == OperationHistoryEvent.UNDONE  || eventType == OperationHistoryEvent.REDONE ||
						eventType == OperationHistoryEvent.OPERATION_ADDED || eventType == OperationHistoryEvent.OPERATION_REMOVED) {
					saveResultsAction.setEnabled(false);
				}
			}
		});
		JFaceResources.getFontRegistry().put(MyToolTip.HEADER_FONT, JFaceResources.getFontRegistry().getBold(JFaceResources.getDefaultFont().getFontData()[0].getName()).getFontData());
		MyToolTip toolTip = new MyToolTip(tableViewer.getControl());
		toolTip.setShift(new Point(-5, -5));
		toolTip.setHideOnMouseDown(false);
		toolTip.activate();
	}
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
		
	}
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(identifyEvaluateAction);
		manager.add(saveResultsAction);
	}
	
	private void makeActions() {
		identifyEvaluateAction = new Action() {
			public void run() {
				boolean wasAlreadyOpen = false;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView(ID);
				
				if(viewPart != null) {
					page.hideView(viewPart);
					wasAlreadyOpen = true;
					//ISelectionProvider selProvider = viewPart.getSite().getSelectionProvider();
				}
				CompilationUnitCache.getInstance().clearCache();
				candidateEvaluateTable = getTable();
				tableViewer.setContentProvider(new ViewContentProvider());
				saveResultsAction.setEnabled(true);
				if(wasAlreadyOpen)
					openPackageExplorerViewPart();
			}
		};
		identifyEvaluateAction.setToolTipText("Identify Problem");
		identifyEvaluateAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		identifyEvaluateAction.setEnabled(false);
		
		saveResultsAction = new Action() {
			public void run() {
				saveResults();
			}
		};
	
		saveResultsAction.setToolTipText("Save Results");
		saveResultsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		saveResultsAction.setEnabled(false);
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				CandidateEvaluate candidate = (CandidateEvaluate)selection.getFirstElement();
				if(candidate.getSourceClassTypeDeclaration() != null && candidate.getTargetClassTypeDeclaration() != null) {
					IFile sourceFile = candidate.getSourceIFile();
					IFile targetFile = candidate.getTargetIFile();
					try {
						IJavaElement targetJavaElement = JavaCore.create(targetFile);
						JavaUI.openInEditor(targetJavaElement);
						IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
						ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
						List<Position> positions = candidate.getPositions();
						//AnnotationModel annotationModel = (AnnotationModel)sourceEditor.getDocumentProvider().getAnnotationModel(sourceEditor.getEditorInput());
						//Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
						/*while(annotationIterator.hasNext()) {
							Annotation currentAnnotation = annotationIterator.next();
							if(currentAnnotation.getType().equals(SliceAnnotation.EXTRACTION)) {
								annotationModel.removeAnnotation(currentAnnotation);
							}
						}*/
						/*for(Position position : positions) {
							SliceAnnotation annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, candidate.getAnnotationText());
							annotationModel.addAnnotation(annotation, position);
						}*/
						Position firstPosition = positions.get(0);
						Position lastPosition = positions.get(positions.size()-1);
						int offset = firstPosition.getOffset();
						int length = lastPosition.getOffset() + lastPosition.getLength() - firstPosition.getOffset();
						sourceEditor.setHighlightRange(offset, length, true);

						//EvaluateVisualizationDataSingleton.setData((candidate).getFeatureEnvyVisualizationData());
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IViewPart viewPart = page.findView(ID);
						if(viewPart != null)
							page.hideView(viewPart);
						page.showView(ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
	}
	private void hookDoubleClickAction() {
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	public void setFocus() {
		tableViewer.getControl().setFocus();

	}
	public void dispose() {
		super.dispose();
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
	}
	private void saveResults() {
		FileDialog fd = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.SAVE);
		fd.setText("Save Results");
		String[] filterExt = { "*.txt" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if(selected != null) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(selected));
				Table table = tableViewer.getTable();
				TableColumn[] columns = table.getColumns();
				for(int i=0; i<columns.length; i++) {
					if(i == columns.length-1)
						out.write(columns[i].getText());
					else
						out.write(columns[i].getText() + "\t");
				}
				out.newLine();
				for(int i=0; i<table.getItemCount(); i++) {
					TableItem tableItem = table.getItem(i);
					for(int j=0; j<table.getColumnCount(); j++) {
						if(j == table.getColumnCount()-1)
							out.write(tableItem.getText(j));
						else
							out.write(tableItem.getText(j) + "\t");
					}
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private MyClass[] getTable() {
		MyClass[] table = null;
		
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService(); 
			if(ASTReader.getSystemObject() != null && selectedProject.equals(ASTReader.getExaminedProject())) {
				new ASTReader(selectedProject, ASTReader.getSystemObject(), null);
				
			}
		     else {
		    	 System.out.println(" Ce fait !!!!!");
				ps.busyCursorWhile( new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						new ASTReader(selectedProject, monitor);
					}
				});
			}
			SystemObject systemObject = ASTReader.getSystemObject();
			Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
			if(selectedPackageFragmentRoot != null) {
				classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragmentRoot));
			}
			else if(selectedPackageFragment != null) {
				classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragment));
			}
			else if(selectedCompilationUnit != null) {
				classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedCompilationUnit));
			}
			else if(selectedType != null) {
				classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedType));
			}
			else {
				classObjectsToBeExamined.addAll(systemObject.getClassObjects());
			}
		
			final Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
			final List<String> classNamesToBeExaminedList = new ArrayList<String>();
			for(ClassObject classObject : classObjectsToBeExamined) {
				if(!classObject.isEnum())
					classNamesToBeExamined.add(classObject.getName());
				    classNamesToBeExaminedList.add(classObject.getName());
			}
			System.out.println(" Class list "+classNamesToBeExaminedList);
			
			MySystem system = new MySystem(systemObject, false);
			final DistanceMatrix distanceMatrix = new DistanceMatrix(system);
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					distanceMatrix.generateDistances(monitor);
				}
			});
			final List<MyClass> ClassCandidateList = new ArrayList<MyClass>();

			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					ClassCandidateList.addAll(distanceMatrix.getExtractClassCandidateEvaluate(classNamesToBeExamined, monitor));
				}
			});
			//ExtractClassCandidateEvaluate currentSystem = new ExtractClassCandidateEvaluate("current system");
			table = new MyClass [ClassCandidateList.size() + 1];
			//table[0] = new CurrentSystem (distanceMatrix) ;
			int counter = 0;
			for(MyClass candidate : ClassCandidateList) {
				table[counter] = candidate;
				counter++;
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return table;	

	}
	private void openPackageExplorerViewPart() {
		try {
			EvaluateVisualizationDataSingleton.setCandidates(candidateEvaluateTable);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart viewPart = page.findView(ID);
			//CodeSmellPackageExplorer.CODE_SMELL_TYPE = CodeSmellType.FEATURE_ENVY;
			if(viewPart != null)
				page.hideView(viewPart);
			page.showView(ID);

		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	public void setSelectedLine(final String str) {
		Table table = tableViewer.getTable();
		for(int i=0; i<table.getItemCount(); i++) {
			Object tableElement = tableViewer.getElementAt(i);
			String candidate = (String)tableElement;
			if(candidate.equals(str)) {
				table.setSelection(i);
				break;
			}
		}
	}
	protected class MyToolTip extends ToolTip {
		//public static final String HEADER_BG_COLOR = Policy.JFACE + ".TOOLTIP_HEAD_BG_COLOR";
		//public static final String HEADER_FG_COLOR = Policy.JFACE + ".TOOLTIP_HEAD_FG_COLOR";
		public static final String HEADER_FONT = Policy.JFACE + ".TOOLTIP_HEAD_FONT";

		public MyToolTip(Control control) {
			super(control);
		}

		protected Composite createToolTipContentArea(Event event, Composite parent) {
			Composite comp = new Composite(parent,SWT.NONE);
			GridLayout gl = new GridLayout(1,false);
			gl.marginBottom=0;
			gl.marginTop=0;
			gl.marginHeight=0;
			gl.marginWidth=0;
			gl.marginLeft=0;
			gl.marginRight=0;
			gl.verticalSpacing=1;
			comp.setLayout(gl);

			Composite topArea = new Composite(comp,SWT.NONE);
			GridData data = new GridData(SWT.FILL,SWT.FILL,true,false);
			data.widthHint=200;
			topArea.setLayoutData(data);
			topArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

			gl = new GridLayout(1,false);
			gl.marginBottom=2;
			gl.marginTop=2;
			gl.marginHeight=0;
			gl.marginWidth=0;
			gl.marginLeft=5;
			gl.marginRight=2;

			topArea.setLayout(gl);

			Label label = new Label(topArea,SWT.NONE);
			label.setText("APPLY FIRST");
			label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			label.setFont(JFaceResources.getFontRegistry().get(HEADER_FONT));
			//label.setForeground(JFaceResources.getColorRegistry().get(HEADER_FG_COLOR));
			label.setLayoutData(new GridData(GridData.FILL_BOTH));

			Table table = tableViewer.getTable();
			Point coords = new Point(event.x, event.y);
			TableItem item = table.getItem(coords);
			if(item != null) {}
			return comp;
		}

		protected boolean shouldCreateToolTip(Event event) {
			Table table = tableViewer.getTable();
			Point coords = new Point(event.x, event.y);
			TableItem item = table.getItem(coords);
			if(item != null) {
				//List<CandidateRefactoring> prerequisiteRefactorings = getPrerequisiteRefactorings((CandidateRefactoring)item.getData());
				//if(!prerequisiteRefactorings.isEmpty())
					return true;
			}
			return false;
		}
	}
}
