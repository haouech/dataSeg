package com.pfe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import dl_query.DLQueryEngine;
import dl_query.DLQueryPrinter;
import com.pfe.entities.Activity;

public class OntologyManager {

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private Reasoner reasoner;
	private TimeManager timeManager;
	
	public Map<String, String> unique_properties = new HashMap<String, String>();
	public Set<String> properties = new HashSet<String>();
	
	final private String OWL_FILE_KEY = "owlfilename";
	final private String documentIRI = "http://www.semanticweb.org/haouech/ontologies/2017/10/activities-0-1";

	private static OntologyManager instance = null;
	
	private OntologyManager() {
		manager = OWLManager.createOWLOntologyManager();
		try {
			FileInputStream propFile = new FileInputStream(new File("resources/ontology.properties"));
			Properties properties = new Properties();
			properties.load(propFile);
			String owlfilename = properties.getProperty(OWL_FILE_KEY);
			ontology = manager.loadOntologyFromOntologyDocument(new File(owlfilename));
			propFile.close();
		} catch (OWLOntologyCreationException e) {
			System.out.println("Cannot load owl file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Properties file: \"ontology.properties\" not found");
			e.printStackTrace();
		}
		factory = manager.getOWLDataFactory();
		reasoner = new Reasoner(ontology);
		timeManager = TimeManager.getInstance();
	}
	
	public static OntologyManager getInstance() {
		if(instance == null) {
			instance = new OntologyManager();
		}
		return instance;
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
		String query = createQuery();
		System.out.println("Query: " + query);
		Set<OWLClass> equivalentClasses = queryEquivalentClasses(query, ontology);
		if(equivalentClasses.size() > 0) {
			OWLClass c = equivalentClasses.iterator().next();
			Activity activity = new Activity(c.getIRI().getFragment());
			activity.setStartTime(timeManager.getCurrentTime());
			activity.setAsserted(true);
			activity.setSpecific(true);
			activities.add(activity);
			return activities;
		}
		Set<OWLClass> subClasses = querySubClasses(query, ontology);
		for(OWLClass c : subClasses) {
			Activity activity = new Activity(c.getIRI().getFragment());
			activity.setStartTime(timeManager.getCurrentTime());
			NodeSet<OWLClass> classes = reasoner.getSubClasses(c, false);
			if(classes.getFlattened().size() == 1) {
				activity.setSpecific(true);
			}
			activities.add(activity);
		}
		return activities;		
	}
	
	private Set<OWLClass> querySubClasses(String query, OWLOntology ontology2) {
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), 
															shortFormProvider);	     
		Set<OWLClass> set = dlQueryPrinter.getSubClasses(query);
		return set;
	}


	private Set<OWLClass> queryEquivalentClasses(String query, OWLOntology ontology2) {
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), 
															shortFormProvider);	     
		Set<OWLClass> set = dlQueryPrinter.getEquivalentClasses(query);
		return set;
	}

	private void addProperty(String sensor, String time) {
		OWLClass cls = factory.getOWLClass(IRI.create(documentIRI+"#"+sensor));
		Set<OWLClass> set = reasoner.getSuperClasses(cls, true).getFlattened();
		for (OWLClass c : set) {
			String type = c.getIRI().getFragment();
			// Fix name typos
			type = type.equals("Addings")?"Adding":type;
			type = type.equals("DrinkType")?"HotDrinkType":type;
			
			if(type.equals("Location")) {
				unique_properties.put("hasLocation some ", sensor);
//				System.out.println("hasLocation some " + sensor);
			} else {
				properties.add("has" + type + " some " + sensor);
//				System.out.println("has" + type + " some " + sensor);
			}
		}
	}

	private void deleteProperty(String sensor, String time) {
		OWLClass cls = factory.getOWLClass(IRI.create(documentIRI+"#"+sensor));
		Set<OWLClass> set = reasoner.getSuperClasses(cls, true).getFlattened();
		for (OWLClass c : set) {
			String type = c.getIRI().getFragment();
			// Fix name typos
			type = type.equals("Addings")?"Adding":type;
			type = type.equals("DrinkType")?"HotDrinkType":type;
			
			if(type.equals("Location")) {
				unique_properties.remove("hasLocation some ");
			} else {
				properties.remove("has" + type + " some " + sensor);
			}
		}
	}

	private String createQuery() {
		String query = "";
		Iterator<String> it = properties.iterator();
		while(it.hasNext()) {
			query += it.next() + " and ";
		}
		for(Map.Entry<String, String> entry : unique_properties.entrySet()) {
			query += entry.getKey() + " " + entry.getValue() + " and " ; 
		}
		if(!query.equals("")) {
			// remove trailing "and"
			query = query.substring(0, query.length()-5);
		}
		return query;
	}
	
	public Set<OWLClass> getActivitySubClasses(Activity activity) {
		OWLClass classe = factory.getOWLClass(IRI.create(documentIRI + activity.getLabel()));
		return reasoner.getSubClasses(classe, true).getFlattened();
	}
	
	public void clearData() {
		unique_properties.clear();
		properties.clear();
	}
	
}
