/*
 *  JOrtho
 *
 *  Copyright (C) 2005-2016 by i-net software
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version. 
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 *  
 * Created on 19.04.2016
 */
package com.inet.jorthodictionaries;

import java.util.Properties;

import com.inet.jortho.Utils;

/**
 * 
 * @author Volker Berlin
 */
public class BookGenerator_pt extends BookGenerator {

    @Override
    boolean isValidLanguage( String word, String wikiText ) {
        if( wikiText.indexOf("={{-pt-}}=") < 0){
            return false;
        }

        // plural
        Properties props = BookUtils.parseRule( wikiText, "flex.pt", 0 );
        if( props != null ) {
            for( Object w : props.values() ) {
                addValidWord( (String)w );
            }
        }

        // Conjugação
        props = BookUtils.parseRule( wikiText, "conj/pt", 0 );
        if( props != null ) {
            String base = props.getProperty( "1" );
            String type = props.getProperty( "2" );
            if( base != null ) {
                String[] suffixe = null;
                if( "ar".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.ar&action=edit
                    suffixe = new String[]{"ar", "ando", "ado", "o", "as", "a", "amos", "ais", "am", "ava", "avas", "ávamos", "áveis", "avam", "ei", "aste", "ou", "ámos", "astes", "aram", "ara", "aras", "áramos", "áreis", "arei", "arás", "ará", "aremos", "areis", "arão", "aria", "arias", "aríamos", "aríeis", "ariam", "e", "es", "emos", "eis", "em", "asse", "asses", "ássemos", "ásseis", "assem", "ares", "armos", "ardes", "arem", "ai" };
                } else if( "car".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.car&action=edit
                    suffixe = new String[]{"car", "cando", "cado", "co", "cas", "ca", "camos", "cais", "cam", "cava", "cavas", "cávamos", "cáveis", "cavam", "quei", "caste", "cou", "cámos", "castes", "caram", "cara", "caras", "cáramos", "cáreis", "carei", "carás", "cará", "caremos", "careis", "carão", "carias", "caria", "caríamos", "caríeis", "cariam", "que", "ques", "quemos", "queis", "quem", "casse", "casses", "cássemos", "cásseis", "cassem", "cares", "carmos", "cardes", "carem", "cai" };
                } else if( "ear".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.ear&action=edit
                    suffixe = new String[]{"ear", "eando", "eado", "eio", "eias", "eia", "eamos", "eais", "eiam", "eava", "eavas", "eávamos", "eáveis", "eavam", "eei", "easte", "eou", "eamos | 19pte={{{1}}}eámos", "eastes", "earam", "eara", "earas", "eáramos", "eáreis", "earei", "earás", "eará", "earemos", "eareis", "earão", "earia", "earias", "earíamos", "earíeis", "eariam", "eie", "eies", "eemos", "eeis", "eiem", "easse", "easses", "eássemos", "eásseis", "eassem", "eares", "earmos", "eardes", "earem", "eai" };
                } else if( "gar".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.gar&action=edit
                    suffixe = new String[]{"gar", "gando", "gado", "go", "gas", "ga", "gamos", "gais", "gam", "gava", "gavas", "gávamos", "gáveis", "gavam", "guei", "gaste", "gou", "gamos", "gámos", "gastes", "garam", "gara", "garas", "gáramos", "gáreis", "garei", "garás", "gará", "garemos", "gareis", "garão", "garias", "garia", "garíamos", "garíeis", "gariam", "gue", "gues", "guemos", "gueis", "guem", "gasse", "gasses", "gássemos", "gásseis", "gassem", "gares", "garmos", "gardes", "garem", "gai" };
                } else if( "çar".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.%C3%A7ar&action=edit
                    suffixe = new String[]{"çar", "çando", "çado", "ço", "ças", "ça", "çamos", "çais", "çam", "çava", "çavas", "çávamos", "çáveis", "çavam", "cei", "çaste", "çou", "çámos", "çastes", "çaram", "çara", "çaras", "çáramos", "çáreis", "çarei", "çarás", "çará", "çaremos", "çareis", "çarão", "çaria", "çarias", "çaríamos", "çaríeis", "çariam", "ce", "ces", "cemos", "ceis", "cem", "çasse", "çasses", "çássemos", "çásseis", "çassem", "çares", "çarmos", "çardes", "çarem", "çai" };
                } else if( "oar".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.oar&action=edit
                    suffixe = new String[]{ "oar", "oando", "oado", "oo", "oas", "oa", "oamos", "oais", "oam", "oava", "oavas", "oávamos", "oáveis", "oavam", "oei", "oaste", "oou", "oámos", "oastes", "oaram", "oara", "oaras", "oáramos", "oáreis", "oarei", "oarás", "oará", "oaremos", "oareis", "oarão", "oarias", "oaria", "oaríamos", "oaríeis", "oariam", "oe", "oes", "oemos", "oeis", "oem", "oasse", "oasses", "oássemos", "oásseis", "oassem", "oares", "oarmos", "oardes", "oarem", "oai" };
                } else if( "er".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.er&action=edit
                    suffixe = new String[]{"er", "endo", "ido", "o", "es", "e", "emos", "eis", "em", "ias", "ia", "íamos", "íeis", "iam", "i", "este", "eu", "estes", "eram", "era", "eras", "êramos", "êreis", "erei", "erás", "erá", "eremos", "ereis", "erão", "eria", "erias", "eríamos", "eríeis", "eriam", "a", "as", "amos", "ais", "am", "esse", "esses", "êssemos", "êsseis", "essem", "eres", "ermos", "erdes", "erem", "ei" };
                } else if( "cer".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.cer&action=edit
                    suffixe = new String[]{"cer", "cendo", "cido", "ço", "ces", "ce", "cemos", "ceis", "cem", "cia", "cias", "cíamos", "cíeis", "ciam", "ci", "ceste", "ceu", "cestes", "ceram", "ceras", "cera", "cêramos", "cêreis", "cerei", "cerás", "cerá", "ceremos", "cereis", "cerão", "ceria", "cerias", "ceríamos", "ceríeis", "ceriam", "ça", "ças", "çamos", "çais", "çam", "cesse", "cesses", "cêssemos", "cêsseis", "cessem", "ceres", "cermos", "cerdes", "cerem", "cei" };
                } else if( "ger".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.ger&action=edit
                    suffixe = new String[]{ "ger", "gendo", "gido", "jo", "ges", "ge", "gemos", "geis", "gem", "gia", "gias", "gíamos", "gíeis", "giam", "gi", "geste", "geu", "gestes", "geram", "gera", "geras", "gêramos", "gêreis", "gerei", "gerás", "gerá", "geremos", "gereis", "gerão", "geria", "gerias", "geríamos", "geríeis", "geriam", "ja", "jas", "jamos", "jais", "jam", "gesse", "gesses", "gêssemos", "gêsseis", "gessem", "geres", "germos", "gerdes", "gerem", "gei" };
                } else if( "ser".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.ser&action=edit
                    suffixe = new String[]{ "ser", "sendo", "sido", "sou", "és", "é", "somos", "sois", "são", "eras", "era", "éramos", "éreis", "eram", "fui", "foste", "foi", "fomos", "fostes", "foram", "foras", "fora", "fôramos", "fôreis", "serei", "serás", "será", "seremos", "sereis", "serão", "serias", "seria", "seríamos", "seríeis", "seriam", "seja", "sejas", "sejamos", "sejais", "sejam", "fosses", "fosse", "fôssemos", "fôsseis", "fossem", "for", "fores", "formos", "fordes", "forem", "sê", "sede", "seres", "sermos", "serdes", "serem" };
                } else if( "ir".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.ir&action=edit
                    suffixe = new String[]{ "ir", "indo", "ido", "o", "es", "e", "imos", "is", "em", "ia", "ias", "íamos", "íeis", "iam", "i", "iste", "iu", "istes", "iram", "ira", "iras", "íramos", "íreis", "irei", "irás", "irá", "iremos", "ireis", "irão", "iria", "irias", "iríamos", "iríeis", "iriam", "a", "as", "amos", "ais", "am", "isses", "isse", "íssemos", "ísseis", "issem", "ires", "irmos", "irdes", "irem" };
                } else if( "cobrir".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.cobrir&action=edit
                    suffixe = new String[]{ "cobrir", "cobrindo", "coberto", "cubro", "cobres", "cobre", "cobrimos", "cobris", "cobrem", "cobria", "cobrias", "cobríamos", "cobríeis", "cobriam", "cobri", "cobriste", "cobriu", "cobristes", "cobriram", "cobrira", "cobriras", "cobríramos", "cobríreis", "cobrirei", "cobrirás", "cobrirá", "cobriremos", "cobrireis", "cobrirão", "cobriria", "cobririas", "cobriríamos", "cobriríeis", "cobririam", "cubra", "cubras", "cubramos", "cubrais", "cubram", "cobrisse", "cobrisses", "cobríssemos", "cobrísseis", "cobrissem", "cobrires", "cobrirmos", "cobrirdes", "cobrirem" };
                } else if( "gir".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.gir&action=edit
                    suffixe = new String[]{ "gir", "gindo", "gido", "jo", "ges", "ge", "gimos", "gis", "gem", "gia", "gias", "gíamos", "gíeis", "giam", "gi", "giste", "giu", "gistes", "giram", "giras", "gira", "gíramos", "gíreis", "girei", "girás", "girá", "giremos", "gireis", "girão", "giria", "girias", "giríamos", "giríeis", "giriam", "ja", "jas", "jamos", "jais", "jam", "gisse", "gisses", "gíssemos", "gísseis", "gissem", "gires", "girmos", "girdes", "girem" };
                } else if( "uir".equals( type ) ) { // https://pt.wiktionary.org/w/index.php?title=Predefini%C3%A7%C3%A3o:conj.pt.uir&action=edit
                    suffixe = new String[]{"uir", "uindo", "uído", "uo", "uis", "ui", "uímos", "uís", "uem", "uía", "uías", "uíamos", "uíeis", "uíam", "uí", "uíste", "uiu", "uístes", "uíram", "uíra", "uíras", "uíramos", "uíreis", "uirei", "uirás", "uirá", "uiremos", "uireis", "uirão", "uiria", "uirias", "uiríamos", "uiríeis", "uiriam", "ua", "uas", "uamos", "uais", "uam", "uísses", "uísse", "uíssemos", "uísseis", "uíssem", "uíres", "uirmos", "uirdes", "uírem" };
                } else {
                    //TODO more conjugation System.out.println(props);
                }
                if( suffixe != null ) {
                    addWords( base, suffixe );
                }
            }
        }
        return true;
    }

    private void addWords( String base, String... suffixe ) {
        for( String suffix : suffixe ) {
            addValidWord( base + suffix );
        }
    }

    private void addValidWord( String word ) {
        if( word == null ) {
            return;
        }
        word = Utils.replaceUnicodeQuotation( word );
        if( isValidWord( word ) ) {
            addWord( word );
        }
    }
}
