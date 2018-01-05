package dl_query;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by UyumazHakan on 24.05.2014.
 */
public class DLQueryPrinter {

    private final DLQueryEngine dlQueryEngine;
    private final ShortFormProvider shortFormProvider;

    /**
     * @param engine            the engine
     * @param shortFormProvider the short form provider
     */
    public DLQueryPrinter(DLQueryEngine engine,
                          ShortFormProvider shortFormProvider) {
        this.shortFormProvider = shortFormProvider;
        dlQueryEngine = engine;
    }

    /**
     * @param classExpression the class expression to use for interrogation
     */
    public Set<OWLClass> getSubClasses(String query) {
        if (query.length() == 0) {
            System.out.println("No class expression specified");
            return new HashSet<OWLClass>();
        } else {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("\n--------------------------------------------------------------------------------\n");
                sb.append("QUERY:   ");
                sb.append(query);
                sb.append("\n");
                sb.append("--------------------------------------------------------------------------------\n\n");
                // Ask for the subclasses of the specified
                // class expression.
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(
                		query, true);
//                set.addAll(subClasses);
//                printEntities("SubClasses", subClasses, sb);
//                System.out.println(sb.toString());
                return subClasses;
            } catch (ParserException e) {
                System.out.println(e.getMessage());
            }
        }
        return new HashSet<OWLClass>();
    }

    /**
     * @param classExpression the class expression to use for interrogation
     */
    public Set<OWLClass> getEquivalentClasses(String query) {
        if (query.length() == 0) {
            System.out.println("No class expression specified");
            return new HashSet<OWLClass>();
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\n--------------------------------------------------------------------------------\n");
            sb.append("QUERY:   ");
            sb.append(query);
            sb.append("\n");
            sb.append("--------------------------------------------------------------------------------\n\n");
            // Ask for the equivalent classes of the specified
            // class expression (query).
            Set<OWLClass> equivalentClasses = dlQueryEngine
                    .getEquivalentClasses(query);
            return equivalentClasses;
        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }
        return new HashSet<OWLClass>();
    }

    private void printEntities(String name, Set<? extends OWLEntity> entities,
                               StringBuilder sb) {
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        if (!entities.isEmpty()) {
            for (OWLEntity entity : entities) {
                sb.append("\t");
                sb.append(shortFormProvider.getShortForm(entity));
                sb.append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
        sb.append("\n");
    }
}