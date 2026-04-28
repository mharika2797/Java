import java.util.*;

public class SingleResponsibilty {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("phani", "teja", "kesha", "phani"));

        StringConverter converter = new StringConverter(names);
        converter.createDictionary();

        DictionaryPrinter printer = new DictionaryPrinter(converter.getDictionary());
        printer.print();
    }
}

// Responsibility 1: Convert a list of strings into a frequency map (word count dictionary).
// This class only knows how to build the map — it does NOT know how to display or store it.
// If we later change the data structure, only this class needs to change.
class StringConverter {
    private final List<String> items;
    // HashMap keys must use wrapper types (Integer, not int) because generics
    // in Java work with objects, not primitives.
    private final Map<String, Integer> dictionary = new HashMap<>();

    StringConverter(List<String> items) {
        this.items = items;
    }

    public void createDictionary() {
        for (String item : items) {
            // merge(): if key absent, insert value 1; otherwise apply Integer::sum
            dictionary.merge(item, 1, Integer::sum);
        }
    }

    public Map<String, Integer> getDictionary() {
        // Return an unmodifiable view so callers cannot mutate our internal state
        return Collections.unmodifiableMap(dictionary);
    }
}

// Responsibility 2: Display the dictionary.
// If the output format changes (e.g. write to file, JSON, CSV), only this class changes.
// StringConverter stays untouched — that is the Single Responsibility Principle in action.
class DictionaryPrinter {
    private final Map<String, Integer> dictionary;

    DictionaryPrinter(Map<String, Integer> dictionary) {
        this.dictionary = dictionary;
    }

    public void print() {
        dictionary.forEach((word, count) -> System.out.println(word + ": " + count));
    }
}

/*
Notes:
1) Each class must have only one responsibility.
   StringConverter  → builds the frequency map
   DictionaryPrinter → outputs the frequency map

2) The Single Responsibility Principle makes code easier to change:
   - Want to output JSON instead of plain text? Edit only DictionaryPrinter.
   - Want to count characters instead of words? Edit only StringConverter.
   Neither change breaks the other class.
*/
