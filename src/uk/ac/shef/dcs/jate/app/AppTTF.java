package uk.ac.shef.dcs.jate.app;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexSearcher;
import uk.ac.shef.dcs.jate.JATEException;
import uk.ac.shef.dcs.jate.JATEProperties;
import uk.ac.shef.dcs.jate.algorithm.Algorithm;
import uk.ac.shef.dcs.jate.algorithm.TTF;
import uk.ac.shef.dcs.jate.feature.FrequencyTermBased;
import uk.ac.shef.dcs.jate.feature.FrequencyTermBasedFBMaster;
import uk.ac.shef.dcs.jate.model.JATETerm;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by zqz on 24/09/2015.
 */
public class AppTTF extends App {
    public static void main(String[] args) throws JATEException, IOException {
        if (args.length < 1) {
            printHelp();
            System.exit(1);
        }
        String solrHomePath = args[args.length - 3];
        String solrCoreName=args[args.length-2];
        String jatePropertyFile=args[args.length - 1];

        Map<String, String> params = getParams(args);

        List<JATETerm> terms = new AppTTF().extract(solrHomePath,solrCoreName, jatePropertyFile, params);
        String paramValue=params.get("-o");
        write(terms,paramValue);

    }

    @Override
    public List<JATETerm> extract(String solrHomePath, String coreName, String jatePropertyFile, Map<String, String> params) throws IOException, JATEException {
        EmbeddedSolrServer solrServer= new EmbeddedSolrServer(Paths.get(solrHomePath), coreName);
        SolrCore core = solrServer.getCoreContainer().getCore(coreName);
        SolrIndexSearcher searcher = core.getSearcher().get();

        JATEProperties properties = new JATEProperties(jatePropertyFile);
        FrequencyTermBasedFBMaster featureBuilder = new
                FrequencyTermBasedFBMaster(searcher, properties, 0);
        FrequencyTermBased feature = (FrequencyTermBased)featureBuilder.build();
        Algorithm ttf = new TTF();
        ttf.registerFeature(FrequencyTermBased.class.getName(), feature);

        List<JATETerm> terms=ttf.execute(feature.getMapTerm2TTF().keySet());
        terms=applyThresholds(terms, params.get("-t"), params.get("-n"));
        String paramValue=params.get("-c");
        if(paramValue!=null &&paramValue.equalsIgnoreCase("true")) {
            collectTermInfo(searcher.getLeafReader(), terms, properties.getSolrFieldnameJATENGramInfo(),
                    properties.getSolrFieldnameID());
        }

        searcher.close();
        core.close();
        solrServer.close();
        return terms;
    }
}
