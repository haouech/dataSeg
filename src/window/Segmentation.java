package window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class Segmentation {
	
	/**
	 * Initial time window .
	 */
	public Window initWindow;
	public Window currentWindow;
	public OWLDataFactory factory;
	public OWLOntologyManager manager;
	public OWLOntology ontology;
	public Ontology o;
	public IRI documentIRI;
	
	public void init()
	{
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File("activities.owl"));
			System.out.println(ontology);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot load owl file");
			e.printStackTrace();
		}
		documentIRI = manager.getOntologyDocumentIRI(ontology);
		
		//o.setOntology(ontology);	
		factory = manager.getOWLDataFactory();
	}
	public OWLIndividual createIndividual(String name, String className) {
		// OWLIndividual indiv = factory.
		
		OWLClass c = factory.getOWLClass(IRI.create(documentIRI + "#" + className));
		System.out.println("***************");
		System.out.println(c);
		OWLIndividual indiv = factory.getOWLNamedIndividual(IRI.create(documentIRI+"#"+name));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(c, indiv);
		manager.addAxiom(ontology, axiom);
		System.out.println(axiom);
		//manager.saveOntology(ontology);
		return indiv;
	}
	public void recognizeADL(long start, double end, double length, ReasoningMode rm, 
			double windowFactor, double slidingFactor, double changeFactor) {
		initWindow = new Window(start, end, length, rm, windowFactor, slidingFactor, changeFactor);
		currentWindow = initWindow ;
		while (true) 
		{
			if (currentWindow == initWindow) 
			{
				currentWindow.setActive(true);
			}
			List<Activity> ret = new ArrayList<>();
			while (currentWindow.getEndTime() < System.currentTimeMillis() )
			{
				obtainAndAddSensorActivations();
//				if (overlapping???)
				if(ReasoningMode.on_sensor.equals(currentWindow.getStartMode()) || ReasoningMode.at_intervals.equals(currentWindow.getStartMode())) {
					List<Activity> res = doOntologicalAR(currentWindow, o);
					ret.addAll(res);
				}
				else if(ReasoningMode.at_intervals.equals(currentWindow.getStartMode()) /* and interval-elapsed*/)
				{
					List<Activity> res = doOntologicalAR(currentWindow, o);
					ret.addAll(res);
				}
			}
			if(ReasoningMode.on_expiry.equals(currentWindow.getStartMode())) 
			{
				List<Activity> res = doOntologicalAR(currentWindow, o);
				ret.addAll(res);
			}
			//Discard previous sensor activation?????????
//			if()????????????????
			
		}
	}
	
	private void obtainAndAddSensorActivations() {
		// TODO Auto-generated method stub
		
	}

	private List<Activity> doOntologicalAR(Window w, Ontology o) {
//		List<Activity> res = new ArrayList<Activity>();
		List<Activity> list;
		list = callOntology();
		if (list.size() == 1) 
		{
			if (list.get(0).isSpecific()) 
			{
				System.out.println("activity successfully identified");
	//			res.add(list.get(0));
				if(w.isShrinkable() || w.isShrinkableAndExpandable()) {
					if(!attemptShrink(w, o, list.get(0))) {
						System.out.println("more sensor data needed");
						attemptExpansion(w, o, list.get(0));
					}
				}
//				res.add(list.get(0));
			} 
			else 
			{
				System.out.println(" size>1 -> more sensor data needed");
				if(w.isShrinkableAndExpandable()) {
					attemptExpansion(w, o, list.get(0));
				}
			}
		} 
		else 
		{
			//res = list
			//Obtain generic activity labels parent activity?????????????????
			Activity parentActivity = list.get(0);
			if(w.isShrinkableAndExpandable()) {
				attemptExpansion(w, o, parentActivity);
			}
		}
		return list;
	}

	private void attemptExpansion(Window w, Ontology o, Activity activity) {
		if(activity.isSpecific()) {
			if (!activity.isAsserted() && o.getNeededTime() > w.pendingTime())
			{
				w.expand(o.getNeededTime() - w.pendingTime());
			}
		}
		else 
		{
//			if(some activations have been obtained : new info?-)
		}
	}

	private boolean attemptShrink(Window w, Ontology o, Activity activity) {
		if(activity.isAsserted())
		{
			w.shrink();
			return true;
		}
		else if(activity.isExhausted())
		{
			w.shrink();
			return true;
		}
		else 
		{
			if(activity.getTimeToComplete() >= w.pendingTime()) 
			{
				attemptExpansion(w, o, activity);
			}
			return false;
		}
	}

	private List<Activity> callOntology() {
		return null;		
	}
	
}
