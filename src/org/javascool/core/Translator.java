package org.javascool.core;

/**
 * Définit une traduction d'un code Jvs en code Java standard.
 * <p>Cette classe permet de définir des variantes de langage pour une proglet donnée.</p>
 *
 * @serial exclude
 * @see <a href="Translator.java.html">code source</a>
 */
public class Translator {
    /**
     * Renvoie les déclarations d'import spécifiques à cette proglet.
     * <p>Ce sont les déclarations d'import spécifiques dont l'utilisateur a besoin pour que son code Jvs puisse se compiler.</p>
     * <p>- Par exemple: <tt>"import javax.swing.JPanel;import static org.javascool.proglets.maProglet.*;"</tt></p>
     * <p>Note: tous les imports liés aux fonctions de la proglet à l'usage des macros de JavaScool etc.. sont automatiquement prises en charge.</p>
     *
     * @return Renvoie les imports en syntaxe Java (par défaut la chaîne vide).
     */
    public String getImports() {
        return "";
    }

    /**
     * Transforme globalement le code pour passer des constructions spécifiques à Jvs à du java standard.
     * <p>Ce sont souvent des expression régulières appliquées à la chaîne, tout est ici de la responsabilité du concepteur de la proglet.</p>
     * <p>Note: toutes les traductions standard du passage de Jvs à Java sont automatiquement prises en charges.</p>
     * <p>Les portions de code de la forme <tt>/*<i>code-jvs</i> @&lt;nojavac*</tt><tt>/<i>code-java-derive</ii>/*@nojavac>*</tt><tt>/</tt> issus de pseudo-code retraduit en Java par la méthode translate sont traités pour que seul le <i>code-jvs</i> soit affiché en cas d'erreur de syntaxe.</p>
     *
     * @param code Le code Jvs en entrée.
     * @return Le code transformé en Java pour ce qui est spécifique de cette proglet (par défaut la chaîne en entrée).</p>
     */
    public String translate(String code) {
        return code;
    }
    //
    // Utilitaires de scan de code
    //

    /**
     * Détermine les bornes d'un parenthésage.
     * - Exemples de construction:
     * <p><tt>scanConstruct(string, offset, "(", ")", null, true, true)</tt> cerne un parenthésage dans un code.</p>
     * <p><tt>scanConstruct(string, offset, "'", "'", "\'", false, false)</tt> cerne une chaîne entre quotes ' '.</p>
     *
     * @param string  Le contenu du texte à analyser.
     * @param offset  L'index de départ dans le texte à analyser.
     * @param start   Le symbole de parenthèse ouvrante, ou null si non défini.
     * @param stop    Le symbole de parenthèse fermante.
     * @param escape  Le symbole d'échappement de la parenthèse fermante, ou null si non défini.
     * @param recurse Effectue une recherche qui tient compte de parenthèses emboitées, si égal à true.
     * @param code    Passe par dessus les commentaires de programme <tt>/* . . *</tt><tt>/</tt> ou <tt>// . . /n</tt> et les chaînes de caractères <tt>" . . "</tt>, si égal à true.
     * @return Renvoie int[2], l'index du premier caratère et un plus l'index du dernier caractères du parenthésage,
     *         <p>tel que <tt>string.substring(index[0], index[1])</tt> corresponde à la chaîne identifiée, sinon</p>
     *         <p>la valeur <tt>{-1, offset}</tt> si le parenthésage n'est pas trouvé.</p>
     * @see #scanInstruction(String, int)
     * @see #scanComments(String, int, boolean)
     * @see #scanSpaces(String, int)
     */
    public static int[] scanConstruct(String string, int offset, String start, String stop, String escape, boolean recurse, boolean code) {
        int index[] = {-1, offset};
        if ((0 <= offset) && (offset < string.length()) && (stop != null)) {
            index[0] = code ? scanComments(string, offset, false) : scanSpaces(string, offset);
            if (!((index[0] < string.length()) && ((start == null) || string.startsWith(start, index[0])))) {
                return new int[]{-1, offset};
            }
            for (index[1] = index[0] + (start == null ? 0 : start.length()); index[1] < string.length() && !string.startsWith(stop, index[1]); ) {
                if ((escape != null && escape.length() > 0) && string.startsWith(escape, index[1])) {
                    index[1] += escape.length();
                } else if (recurse && (start != null && index[1] > index[0]) && string.startsWith(start, index[1])) {
                    int next[] = scanConstruct(string, index[1], start, stop, escape, recurse, code);
                    if (next[0] == -1) {
                        return new int[]{-1, offset};
                    } else {
                        index[1] = next[1];
                    }
                } else if (code) {
                    index[1] = scanComments(string, index[1], true);
                } else {
                    index[1]++;
                }
            }
            if ((index[1] < string.length()) && string.startsWith(stop, index[1])) {
                index[1] += stop.length();
            } else {
                return new int[]{-1, offset};
            }
        } else {
            return new int[]{-1, offset};
        }
        // -System.err.println(("In: "+string.substring(offset, Math.min(string.length(), offset+16))+"..\t< offset='"+offset+"' start='"+start+"' stop='"+stop+"' index='"+index[0]+", "+index[1]+"'\tstring='"+string.substring(index[0], index[1])+"' />").replace('\n', ' '));
        return index;
    }

    /**
     * Détermine les bornes d'une instruction Java.
     * <p>Scanne une construction de la forme <tt>{ ../..}</tt> ou <tt>../..;</tt></p>
     *
     * @param string Le contenu du texte à analyser.
     * @param offset L'index de départ dans le texte à analyser.
     * @return Renvoie int[2], l'index du premier caratère et un plus l'index du dernier caractères du parenthésage,
     *         <p>tel que <tt>string.substring(index[0], index[1])</tt> corresponde à la chaîne identifiée, sinon</p>
     *         <p>la valeur <tt>{-1, offset}</tt> si le parenthésage n'est pas trouvé.</p>
     */
    public static int[] scanInstruction(String string, int offset) {
        int index[] = scanConstruct(string, offset, "{", "}", null, true, true);
        if (index[0] == -1) {
            index = scanConstruct(string, offset, null, ";", null, false, true);
        }
        return index;
    }

    /**
     * Passe par dessus espaces, commentaires et chaines de caratères dans une portion de texte.
     * <p>Passe par dessus les commentaires de programme <tt>/* . . *</tt><tt>/</tt> ou <tt>// . . /n</tt> et les chaînes de caractères <tt>" . . "</tt>.</p>
     *
     * @param string Le contenu du texte à analyser.
     * @param offset L'index de départ dans le texte à analyser.
     * @param next   Incrémente l'offset en l'absence de structure à scanner, si égal à true.
     * @return L'index qui suit le texte éliminé, donc la valeur de l'offset (ou offset plus un si incémente) si il n'y en a pas.
     */
    public static int scanComments(String string, int offset, boolean next) {
        int offset0 = offset;
        for (boolean loop = true; loop; ) {
            int offset1 = offset;
            offset = scanSpaces(string, offset);
            offset = scanConstruct(string, offset, "/*", "*/", null, false, false)[1];
            offset = scanConstruct(string, offset, "//", "\n", null, false, false)[1];
            offset = scanConstruct(string, offset, "\"", "\"", "\\\"", false, false)[1];
            loop = offset > offset1;
        }
        return next && offset == offset0 ? offset + 1 : offset;
    }

    /**
     * Passe par dessus les espaces dans une portion de texte.
     *
     * @param string Le contenu du texte à analyser.
     * @param offset L'index de départ dans le texte à analyser.
     * @return L'index qui suit les espaces, donc la valeur de l'offset si il n'y en a pas.
     */
    public static int scanSpaces(String string, int offset) {
        for (; offset < string.length() && Character.isWhitespace(string.charAt(offset)); offset++) ;
        return offset;
    }
}
