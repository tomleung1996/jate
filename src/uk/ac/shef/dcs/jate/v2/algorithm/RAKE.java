package uk.ac.shef.dcs.jate.v2.algorithm;

import uk.ac.shef.dcs.jate.v2.JATEException;
import uk.ac.shef.dcs.jate.v2.feature.AbstractFeature;
import uk.ac.shef.dcs.jate.v2.feature.Cooccurrence;
import uk.ac.shef.dcs.jate.v2.feature.FrequencyTermBased;
import uk.ac.shef.dcs.jate.v2.model.JATETerm;
import uk.ac.shef.dcs.jate.v2.model.TermInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zqz on 20/09/2015.
 */
public class RAKE extends Algorithm {

    public static final String SUFFIX_WORD="_WORD";

    @Override
    public List<JATETerm> execute(Set<String> candidates) throws JATEException {
        AbstractFeature feature = features.get(FrequencyTermBased.class.getName()+SUFFIX_WORD);
        validateFeature(feature, FrequencyTermBased.class);
        FrequencyTermBased fFeatureWords = (FrequencyTermBased) feature;

        AbstractFeature feature2 = features.get(Cooccurrence.class.getName()+SUFFIX_WORD);
        validateFeature(feature, Cooccurrence.class);
        Cooccurrence fFeatureCoocurr = (Cooccurrence) feature2;

        List<JATETerm> result = new ArrayList<>();
        boolean collectInfo = termInfoCollector != null;

        for(String tString: candidates) {
            String[] elements = tString.split(" ");
            double score=0;
            for(String word: elements) {
                int freq = fFeatureWords.getTTF(word);
                Map<Integer, Integer> coocurWordIdx2Freq = fFeatureCoocurr.getCoocurrence(word);
                int degree=freq;
                for(int f: coocurWordIdx2Freq.values())
                    degree+=f;

                double wScore = (double) degree/freq;
                score+=wScore;
            }

            JATETerm term = new JATETerm(tString, score);
            if (collectInfo) {
                TermInfo termInfo = termInfoCollector.collect(tString);
                term.setTermInfo(termInfo);
            }
            result.add(term);
        }

        return result;
    }
}