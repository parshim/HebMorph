package org.apache.lucene.analysis.hebrew;

import com.carrotsearch.randomizedtesting.annotations.Repeat;
import com.code972.hebmorph.hspell.LingInfo;
import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by synhershko on 22/06/14.
 */
public class TestStreamLemmasFilterWithOrigin extends BaseTokenStreamWithDictionaryTestCase {
    Analyzer a = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
            StreamLemmasFilter src = null;
            try {
                src = new StreamLemmasFilter(reader, getDictionary(), LingInfo.buildPrefixTree(false));
                src.setKeepOriginalWord(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Analyzer.TokenStreamComponents(src);
        }
    };

    /** blast some random strings through the analyzer */
    public void testRandomStrings() throws Exception {
        checkRandomData(random(), a, 1000*RANDOM_MULTIPLIER);
    }

    /** test basic cases */
    @Repeat(iterations = 100)
    public void testBasics() throws IOException {

        checkOneTerm(a, "books", "books");
        checkOneTerm(a, "book", "book");
        checkOneTerm(a, "steven's", "steven's");
        checkOneTerm(a, "steven\u2019s", "steven's");
        //checkOneTerm(a, "steven\uFF07s", "steven's");

        assertAnalyzesTo(a, "בדיקה", new String[]{"בדיקה", "בדיקה"}, new int[] {0, 0}, new int[]{5 ,5}, new int[]{1,0});
        assertAnalyzesTo(a, "צה\"ל", new String[]{"צה\"ל", "צה\"ל"}, new int[]{0, 0}, new int[]{4, 4}, new int[]{1, 0});
        assertAnalyzesTo(a, "צה''ל", new String[]{"צה\"ל", "צה\"ל"}, new int[]{0, 0}, new int[]{5, 5}, new int[]{1, 0});

        checkAnalysisConsistency(random(), a, true, "בדיקה אחת שתיים", true);
    }
}