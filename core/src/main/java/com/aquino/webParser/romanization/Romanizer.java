/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.romanization;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author alex
 */
public class Romanizer {
     
    private static final List<String> FIRST_CONSONANTS = 
            Collections.unmodifiableList(Arrays.asList(
                    "g","gg","n","d","dd","l","m","b",
                    "bb","s","ss","","j","jj","ch","k",
                    "t","p","h"));
    
    private static final List<String> VOWELS = 
            Collections.unmodifiableList(Arrays.asList(
                    "a","ae","ya","ye","eo","e","yeo","ye",
                    "o","wa","wae","oi","yo","u","weo","ue",
                    "wi","yu","eu","eui","i"));
    
    private static final List<String> LAST_CONSONANTS = 
            Collections.unmodifiableList(Arrays.asList(
                    "","g","gg","gs","n","nj","n","d","l",
                    "g","lm","lb","ls","lt","lp","lh","m",
                    "b","bs","s","ss", "ng","j","ch","k",
                    "t","p","h"));
                    
    private static int previousFirstConsonant;
    
    public static String hangulToRoman(String hangul) {
        StringBuilder sentenceBuilder = new StringBuilder();
        
        char[] c = hangul.toCharArray();
        for (int i = c.length-1; i >= 0; i--) {
            sentenceBuilder.insert(0,hangulToRoman(c[i]));
        }
        previousFirstConsonant = -1;
        return StringUtils.capitalize(sentenceBuilder.toString());
    }
    
    private static String hangulToRoman(char hangul) {
        
        int hangulDecimal;    
        StringBuilder syllableBuilder = new StringBuilder();
        hangulDecimal = (int) hangul;
        
        if (hangulDecimal > 55204 || hangulDecimal < 44032) {
            previousFirstConsonant = -1;
            return String.valueOf(hangul);
        }
            
        hangulDecimal -= 44032;
        int firstConsonant = hangulDecimal/588;
        int vowel = (hangulDecimal % 588)/28;
        int lastConsonant = (hangulDecimal % 588) % 28;
        
        if(lastConsonant == 7 || lastConsonant == 19 ||
                lastConsonant == 20 || lastConsonant == 22 ||
                lastConsonant == 23 || lastConsonant == 25 || 
                lastConsonant == 27)
            lastConsonant = handleEdgeCases(lastConsonant);
            
        syllableBuilder.append(FIRST_CONSONANTS.get(firstConsonant));
        syllableBuilder.append(VOWELS.get(vowel));
        syllableBuilder.append(LAST_CONSONANTS.get(lastConsonant));
        
        previousFirstConsonant = firstConsonant;
        return syllableBuilder.toString();
    }
    
    private static int handleEdgeCases(int lastConsonant) {
        if(previousFirstConsonant == -1) 
            return lastConsonant;
        if(previousFirstConsonant != 11)
            return 25;
        else return lastConsonant;
    }
    
    static String transliterateHangulToRoman(String hangul) {
    return hangul.codePoints().boxed().collect(getHangulToRoman());
    }
    private static Collector<Integer, ?, String> getHangulToRoman() {
        final class Accumulation {

            private final StringBuilder syllableBuilder = new StringBuilder();

            private Integer previousSyllable;

            void accumulate(Integer syllable) {
                if (syllable < 0xAC00 || syllable > 0xD7A3) {
                  syllableBuilder.appendCodePoint(syllable);
                  previousSyllable = null;
                }
                else {
                  syllableBuilder.append(transliterateHangulSyllableToRoman(syllable, previousSyllable));
                  previousSyllable = syllable;
                }
            }
            Accumulation combine(Accumulation other) {
                throw new IllegalStateException("This reduction can not be performed in parallel.");
            }
            String finish() {
                return syllableBuilder.toString();
            }
        }
        return Collector.of(Accumulation::new, Accumulation::accumulate, Accumulation::combine, Accumulation::finish);
  }
  private static String transliterateHangulSyllableToRoman(Integer syllableAsCodePoint, Integer previousSyllableAsCodePoint) {
    throw new UnsupportedOperationException("Not implemented.");
  }
   
  
}
