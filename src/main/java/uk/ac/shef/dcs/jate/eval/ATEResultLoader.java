package uk.ac.shef.dcs.jate.eval;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import uk.ac.shef.dcs.jate.model.JATETerm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * load the output of an App class into memory.
 *
 */
public class ATEResultLoader {

    public static List<String> load(String jsonFile) throws FileNotFoundException, UnsupportedEncodingException {
        Gson gson = new Gson();
        List<JATETerm> terms=gson.fromJson(new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(jsonFile), StandardCharsets.UTF_8)), new TypeToken<List<JATETerm>>(){}.getType());

        List<String> result = new ArrayList<>();
        for(JATETerm o: terms){
            result.add(o.getString());
        }

        return result;
    }

    public static List<List<String>> loadJATE1(String jate1outputfile) throws IOException {
        List<String> lines = FileUtils.readLines(new File(jate1outputfile));
        List<List<String>> out = new ArrayList<>(lines.size());
        for(String l : lines){
            String terms = l.split("\t\t\t")[0];
            List<String> variants = new ArrayList<>();
            for(String t: terms.split("\\|")){
                variants.add(t.trim());
            }
            out.add(variants);
        }
        return out;
    }

}