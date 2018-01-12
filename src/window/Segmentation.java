package window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import dl_query.DLQueryEngine;
import dl_query.DLQueryPrinter;

public class Segmentation {
	
	/**
	 * Initial time window .
	 */
	public Window initWindow;
	public Window currentWindow;
	public Ontology o;
	public Map<Window,Set<Activity>> map = new HashMap<>();
	public String documentIRI = "http://www.semanticweb.org/haouech/ontologies/2017/10/activities-0-1";
	public OWLDataFactory factory;
	public OWLOntologyManager manager;
	public OWLOntology ontology;
	public Map<String, String> unique_properties = new HashMap<String, String>();
	public Set<String> properties = new HashSet<String>();
	public static int id = 0;
	public Reasoner reasoner;
	public OWLIndividual indiv;
	
	public void init()
	{
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File("ADL-0.2.owl"));
			System.out.println(ontology);
		} catch (OWLOntologyCreationException e) {
			System.out.println("Cannot load owl file");
			e.printStackTrace();
		}
		//documentIRI = manager.getOntologyDocumentIRI(ontology);
		factory = manager.getOWLDataFactory();
		reasoner = new Reasoner(ontology);
		indiv = createIndividual("adlActivity", "ADLActivity");
	}
	
	public OWLIndividual createIndividual(String name, String className) {
		OWLClass c = factory.getOWLClass(IRI.create(documentIRI + "#" + className));
//		System.out.println("***************");
//		System.out.println(c);
		OWLIndividual indiv = factory.getOWLNamedIndividual(IRI.create(documentIRI+"#"+name));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(c, indiv);
		manager.addAxiom(ontology, axiom);
//		System.out.println(axiom);
		return indiv;
	}
	
	public void addProperty(String sensor, String duration) {
		OWLClass cls = factory.getOWLClass(IRI.create(documentIRI+"#"+sensor));
		Set<OWLClass> set = reasoner.getSuperClasses(cls, true).getFlattened();
		// à voir!!!
		for (OWLClass c : set) {
			String type = c.getIRI().getFragment();
			if(type.equals("Addings")) {
				type = "Adding";
			} else if(type.equals("DrinkType")) {
				type = "HotDrinkType";
			}
			OWLIndividual loc = createIndividual(sensor+id, sensor);
			id++;
			OWLDataProperty hasDuration = factory.getOWLDataProperty(IRI
		            .create(documentIRI + "#hasDuration"));
			OWLDataPropertyAssertionAxiom axiom2 = factory
			            .getOWLDataPropertyAssertionAxiom(hasDuration, loc, duration);
			    
			OWLObjectProperty hasLocation = factory.getOWLObjectProperty(IRI
		            .create(documentIRI + "#has" + type));
			OWLObjectPropertyAssertionAxiom axiom1 = factory
		            .getOWLObjectPropertyAssertionAxiom(hasLocation, indiv, loc);
			AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
		    AddAxiom addAxiom2 = new AddAxiom(ontology, axiom2);

		    // Now we apply the change using the manager.
		    manager.applyChange(addAxiom1);
		    manager.applyChange(addAxiom2);
			if(type.equals("Location")) {
				unique_properties.put("hasLocation some ", sensor);
				System.out.println("hasLocation some " + sensor);
			} else {
				properties.add("has" + type + " some " + sensor);
				System.out.println("has" + type + " some " + sensor);
			}
		}
	}
	public void deleteProperty(String sensor, String duration) {
		OWLClass cls = factory.getOWLClass(IRI.create(documentIRI+"#"+sensor));
		Set<OWLClass> set = reasoner.getSuperClasses(cls, true).getFlattened();
		// à voir!!!
		for (OWLClass c : set) {
			String type = c.getIRI().getFragment();
			if(type.equals("Addings")) {
				type = "Adding";
			} else if(type.equals("DrinkType")) {
				type = "HotDrinkType";
			}
			OWLIndividual loc = createIndividual(sensor+id, sensor);
			id++;
			OWLDataProperty hasDuration = factory.getOWLDataProperty(IRI
		            .create(documentIRI + "#hasDuration"));
			OWLDataPropertyAssertionAxiom axiom2 = factory
			            .getOWLDataPropertyAssertionAxiom(hasDuration, loc, duration);
			    
			OWLObjectProperty hasLocation = factory.getOWLObjectProperty(IRI
		            .create(documentIRI + "#has" + type));
			OWLObjectPropertyAssertionAxiom axiom1 = factory
		            .getOWLObjectPropertyAssertionAxiom(hasLocation, indiv, loc);
			AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
		    AddAxiom addAxiom2 = new AddAxiom(ontology, axiom2);

		    // Now we apply the change using the manager.
		    manager.applyChange(addAxiom1);
		    manager.applyChange(addAxiom2);
			if(type.equals("Location")) {
				unique_properties.remove("hasLocation some ");
			} else {
				properties.remove("has" + type + " some "+sensor);
			}
		}
	}
	
	public Set<OWLClass> DLQueryEquivalentClasses(String query, OWLOntology o) {
		Reasoner reasoner = new Reasoner(o);	
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), 
															shortFormProvider);	     
		Set<OWLClass> set = dlQueryPrinter.getEquivalentClasses(query);
		return set;
	}
	
	public Set<OWLClass> DLQuerySubClasses(String query, OWLOntology o) {
		Reasoner reasoner = new Reasoner(o);	
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), 
															shortFormProvider);	     
		Set<OWLClass> set = dlQueryPrinter.getSubClasses(query);
		return set;
	}

	public void recognizeADL(long start, double length, ReasoningMode rm, 
			double windowFactor, double slidingFactor, double changeFactor) {
		initWindow = new Window(start, length, rm, windowFactor, slidingFactor, changeFactor);
		currentWindow = initWindow ;
		while (true) 
		{
			if (currentWindow == initWindow) 
			{
				currentWindow.setActive(true);
			}
			Set<Activity> ret = new HashSet<>();
			while (currentWindow.getEndTime() > System.currentTimeMillis() )
			{
				obtainAndAddSensorActivations();
				//if overlapping = true
				if (currentWindow.getSlidingFactor() < 1  && !currentWindow.getOverlappingWindow().isActive())
				{
					double s = currentWindow.getSlidingFactor()*currentWindow.getLength()+currentWindow.getStartTime();
					currentWindow.setOverlappingWindow(new Window(s, length, rm, windowFactor, slidingFactor, changeFactor));
				}
				if(ReasoningMode.on_sensor.equals(currentWindow.getStartMode()) || ReasoningMode.at_intervals.equals(currentWindow.getStartMode())) {
					List<Activity> res = doOntologicalAR(currentWindow);
					ret.addAll(res);
				}
				else if(ReasoningMode.at_intervals.equals(currentWindow.getStartMode()) /* and interval-elapsed*/)
				{
					List<Activity> res = doOntologicalAR(currentWindow);
					ret.addAll(res);
				}
			}
			if(ReasoningMode.on_expiry.equals(currentWindow.getStartMode())) 
			{
				List<Activity> res = doOntologicalAR(currentWindow);
				ret.addAll(res);
			}
			discardPreviousSensorActivation();
			map.put(currentWindow, ret);
			//If overlapping true 
			if(currentWindow.getSlidingFactor() == 1) {
				currentWindow.setActive(false);
				currentWindow = new Window(System.currentTimeMillis(), length, rm, windowFactor, slidingFactor, changeFactor);
			}
		}
	}
	
	private void discardPreviousSensorActivation() {
		o.setSensorSet(new ArrayList<>());
		currentWindow.setSet(null);
	}

	private void obtainAndAddSensorActivations() {
		currentWindow.setSet(ObtainSet());
	}

	private List<String> ObtainSet() {
		List<String> s = new ArrayList<>();
		s.add("Kitchen@20");
		s.add("Milk@20");
		return s;
	}

	public List<Activity> doOntologicalAR(Window w) {
		List<Activity> list;
		list = callOntology(w.getSet());
		if(list.size()==0) {
			return list;
		}
		if (list.size() == 1) 
		{
			Activity currentActivity = list.get(0);
			if (currentActivity.isSpecific()) 
			{
				System.out.println("One possible activity identified");
				if(w.isShrinkable() || w.isShrinkableAndExpandable()) {
					if(!attemptShrink(w, currentActivity)) {
						System.out.println("more sensor data needed");
						attemptExpansion(w, currentActivity);
					}
				}
			} 
			else 
			{
				System.out.println("more sensor data needed");
				if(w.isShrinkableAndExpandable()) {
					attemptExpansion(w, currentActivity);
				}
			}
		} 
		else 
		{
			//Obtain generic activity labels parent activity?????????????????
//			Activity parentActivity = list.get(0).getParent();
//			if(w.isShrinkableAndExpandable()) {
//				attemptExpansion(w, parentActivity);
//			}
		}
		return list;
	}

	private void attemptExpansion(Window w, Activity activity) {
		if(activity.isSpecific() && !activity.isAsserted() && 
				activity.getTimeToComplete() > w.pendingTime()){
			w.expand(activity.getTimeToComplete() - w.pendingTime());
		}
		else if(w.getSet().size()>0) {
			List<Subclass> subclasses = obtainSubClasses(activity);
			double maxDuration = getMaxDuration(subclasses);
			double remainingDuration = w.getStartTime()+maxDuration-System.currentTimeMillis();
			if(remainingDuration >= w.pendingTime()) 
			{
				w.expand(remainingDuration - w.pendingTime());
			}
		}
	}
	
	private double getMaxDuration(List<Subclass> subclasses) {
		if(subclasses == null) {
			return 0.0;
		}
		double max = subclasses.get(0).getMaxDuration();
		for(Subclass s : subclasses) {
			max = Math.max(max, s.getMaxDuration());
		}
		return max;
	}

	private List<Subclass> obtainSubClasses(Activity activity) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean attemptShrink(Window w, Activity activity) {
		if(activity.isAsserted() || activity.isExhausted()) {
			w.shrink();
			System.out.println("Activity identified: " + activity.getLabel());
			return true;
		}
		if(activity.getTimeToComplete() >= w.pendingTime()) {
			attemptExpansion(w, activity);
		}
		return false;
	}

	public List<Activity> callOntology(List<String> dataSet) {
		List<Activity> activities = new ArrayList<Activity>();
		for (String s : dataSet) {
			String[] parts = s.split("@");
			String time = parts[0];
			String sensor = parts[1]; 
			String state = parts[2];
			if(state.equals("on")) {
				addProperty(sensor, time);
			} else {
				deleteProperty(sensor, time);
			}
		}	
		String query = getQuery();
		System.out.println("Query: " + query);
		Set<OWLClass> equivalentClasses = DLQueryEquivalentClasses(query, ontology);
		if(equivalentClasses.size() > 0) {
			OWLClass c = equivalentClasses.iterator().next();
			Activity activity = new Activity(c.getIRI().getFragment());
			activity.setAsserted(true);
			activity.setSpecific(true);
			activities.add(activity);
			return activities;
		}
		Set<OWLClass> subClasses = DLQuerySubClasses(query, ontology);
		for(OWLClass c : subClasses) {
			Activity activity = new Activity(c.getIRI().getFragment());
			OWLClass cl = factory.getOWLClass(IRI.create(documentIRI + "#" + activity.getLabel()));
			NodeSet<OWLClass> classes = reasoner.getSubClasses(cl, false);
			if(classes.getFlattened().size() == 1) {
				activity.setSpecific(true);
			}
			activities.add(activity);
		}
		return activities;		
	}
	
	public String getQuery() {
//		if(properties.size() == 0 && unique_properties.size() == 0) {
//			return "";
//		}
		String query = "";
		if(properties.size() > 0) {
			Iterator<String> it = properties.iterator();
			query = it.next();
			while(it.hasNext()) {
				query = query + " and " +it.next();
			}			
		}
		if(!query.equals("")) {
			query += " and ";
		}
		for(Map.Entry<String, String> entry : unique_properties.entrySet()) {
			query += entry.getKey() + " " + entry.getValue() + " and " ; 
		}
		if(query.substring(query.length()-5, query.length()).equals(" and ")) {
			query = query.substring(0, query.length()-5);
		}
		return query;
	}
	
	public String getDuration(String cls) {
		createIndividual(cls+"indiv", cls);
		String sparql = "SELECT ?duration "
				+ "WHERE {"
				+ "?ind rdf:type ns:"+cls+" ."
				+ "?ind ns:hasDurationInMin ?duration"
				+ "}";

		Query qry = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.create(qry);
		ResultSet rs = qe.execSelect();
		String res = "";
		while(rs.hasNext())
		{
		 QuerySolution sol = rs.nextSolution();
		 RDFNode duration = sol.get("duration"); 
		 res = duration.toString();
		}
		qe.close(); 	
		return res;
	}
	
}