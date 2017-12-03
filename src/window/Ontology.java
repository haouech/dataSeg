package window;

import org.semanticweb.owlapi.model.OWLOntology;

public class Ontology {
	
	private long neededTime;
	private OWLOntology ontology;
	
	public Ontology() {
		
	}
	
	public Ontology(OWLOntology o) {
		this.ontology = o;
	}
	
	public long getNeededTime() {
		return neededTime;
	}

	public void setNeededTime(long neededTime) {
		this.neededTime = neededTime;
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}
	
	public void setOntology(OWLOntology o) {
		this.ontology = o;
	}

}
