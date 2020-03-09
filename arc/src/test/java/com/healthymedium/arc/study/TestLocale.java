package com.healthymedium.arc.study;

        import com.healthymedium.arc.core.Locale;
        import org.junit.Test;

public final class TestLocale {

    @Test
    public void testLabels() {
        System.out.println("\ntestLables:");
        for(java.util.Locale locale : Locale.getSupported()) {
            String label = Locale.getLabel(locale);

            String formatter = "\tlang: %s | country: %s | label: %s\n";
            String localeString = String.format(formatter, Locale.getLanguage(locale), Locale.getCountry(locale), Locale.getLabel(locale));
            System.out.print(localeString);
        }
    }

    @Test
    public void testLocales() {
        System.out.println("\ntestLocales:");
        for(java.util.Locale locale : Locale.getSupported()) {
            System.out.println("\t" + locale.getLanguage() + "_" + locale.getCountry());
        }
    }

    @Test
    public void testLocalesDisplayValues() {
        System.out.println("\ntestLocaleDisplayValues:");
        for(java.util.Locale locale : Locale.getSupported()) {
            System.out.println("\t" + locale.getDisplayCountry() + " " + locale.getDisplayLanguage());
        }
    }

    @Test
    public void testLocalesDisplayName() {
        System.out.println("\ntestLocalesDisplayName:");
        for(java.util.Locale locale : Locale.getSupported()) {
            System.out.println("\t" + locale.getDisplayName());
        }
    }
}
