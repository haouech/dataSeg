package test;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import dl_query.DLQueryEngine;
import dl_query.DLQueryPrinter;

public class TestOwl {
	
	
	public static final IRI pizza_iri = IRI
			 .create("http://www.test.com/exemple.owl");
//	"http://www.co-ode.org/ontologies/pizza/pizza.owl");
	/*public void createOntology() throws OWLException {
		 OWLOntologyManager om = OWLManager.createOWLOntologyManager();
//		 File owlFile = new File("ontology.owl");
		 
//		 IRI pressinnov_iri = IRI.create("http://www.pressinnov.com/misc/exemple.owl");		 
		 OWLOntology pressInnovOntology = om.createOntology(pizza_iri);  
	}*/

/*	public void loadOntology() throws OWLException {
		 OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		 OWLOntologyDocumentSource source = new StreamDocumentSource(getClass().
		 getResourceAsStream("./exemple.owl"));
		 om.loadOntologyFromOntologyDocument(source);
		 OWLOntology pressInnovOntology = om.createOntology();
	}*/
	
	/*public void addClass() throws OWLException {
			OWLOntologyManager om = OWLManager.createOWLOntologyManager();
			IRI pressinnov_iri = IRI.create("http://www.pressinnov.com/misc/personne.owl");		 
			OWLOntology pressinnovOntology = om.createOntology(pressinnov_iri); 	
			OWLDataFactory factory = om.getOWLDataFactory();	
	
			OWLClass Sportif = factory.getOWLClass(IRI.create(pressinnov_iri + "#Sportif"));
			OWLClass Footballeur = factory.getOWLClass(IRI.create(pressinnov_iri  + "#Footballeur"));
				
			OWLAxiom axiom = factory.getOWLSubClassOfAxiom(Footballeur, Sportif);
			AddAxiom addAxiom = new AddAxiom(pressinnovOntology, axiom);		
			
			om.applyChange(addAxiom);		
		}*/

	static  void DLQuery (String query) throws OWLOntologyCreationException, OWLOntologyStorageException{
		 OWLOntologyManager om= OWLManager.createOWLOntologyManager();
//		 IRI pressinnov_iri = IRI.create("http://www.pressinnov.com/misc/exemple.owl");		 
		 OWLOntology pressInnovOntology = om.createOntology(pizza_iri);	
		 
		 OWLDataFactory factory = om.getOWLDataFactory();	
			
		OWLClass Sportif = factory.getOWLClass(IRI.create(pizza_iri + "#Sportif"));
		OWLClass Footballeur = factory.getOWLClass(IRI.create(pizza_iri  + "#Footballeur"));
	
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(Footballeur, Sportif);
		AddAxiom addAxiom = new AddAxiom(pressInnovOntology, axiom);	
		om.applyChange(addAxiom);		

		OWLIndividual john = factory.getOWLNamedIndividual(IRI
		            .create(pizza_iri + "#John"));
	    OWLIndividual mary = factory.getOWLNamedIndividual(IRI
		            .create(pizza_iri + "#Mary"));
	    OWLObjectProperty hasWife = factory.getOWLObjectProperty(IRI
	            .create(pizza_iri + "#hasWife"));
		 
	    OWLObjectPropertyAssertionAxiom axiom1 = factory
	            .getOWLObjectPropertyAssertionAxiom(hasWife, john, mary);
	    
	    AddAxiom addAxiom1 = new AddAxiom(pressInnovOntology, axiom1);
	    // Now we apply the change using the manager.
	    om.applyChange(addAxiom1);

	    System.out.println("RDF/XML: ");
	    om.saveOntology(pressInnovOntology, new StreamDocumentTarget(System.out));
	    
	    OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(pressInnovOntology);		
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), shortFormProvider);	     
	        dlQueryPrinter.getSubClasses(query);
		 
	}

	public static void main(String [ ] args){
//		String query = "personne and joue Régulièrement some match";
		String query = "John";

		try {
			DLQuery(query);
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

}
