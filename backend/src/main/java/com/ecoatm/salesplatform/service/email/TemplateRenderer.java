package com.ecoatm.salesplatform.service.email;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Shared {@code {{var}}} / {@code {{!var}}} template substitution engine.
 *
 * <p>Single home for the rendering logic that previously lived inside
 * {@code EmailTemplateServiceImpl} (partial-credit module) — extraction is
 * byte-identical to the original private {@code substitute(...)}: same
 * regex, same {@link Matcher#quoteReplacement(String)} {@code $}-safety,
 * same escape order, same missing-variable warn-and-empty behavior, same
 * null/empty-template guard. {@code EmailTemplateServiceImpl} now
 * delegates to this class instead of duplicating the logic — see
 * {@code EmailTemplateServiceTest} for the regression proof that
 * delegation changed no observable behavior.
 *
 * <p>Two escape modes are exposed because HTML and plain-text/subject
 * contexts need different defaults: {@link #render} HTML-escapes
 * substituted values unless the placeholder opts out with {@code
 * {{!varName}}} (used for HTML bodies); {@link #renderPlain} never
 * escapes (used for subjects and plain-text bodies, which are not HTML
 * contexts — escaping there would corrupt the output rather than protect
 * it).
 */
@Component
public class TemplateRenderer {

    private static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

    /** Matches {@code {{name}}} or {@code {{!name}}}. Group 1 is the
     *  raw-escape marker (present iff the variable opts out of HTML
     *  escaping); group 2 is the variable name. */
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{(!)?([A-Za-z0-9_]+)\\}\\}");

    /**
     * Renders {@code template}, HTML-escaping substituted values unless the
     * placeholder uses the {@code {{!varName}}} raw-escape form. Missing
     * variables substitute to empty string + emit a warn log so they're
     * observable. {@code null}/empty templates are returned unchanged.
     */
    public String render(String template, Map<String, Object> variables) {
        return substitute(template, variables, true);
    }

    /**
     * Renders {@code template} with no HTML escaping at all — for subjects
     * and plain-text bodies, which are not HTML contexts. Missing variables
     * and the null/empty-template guard behave identically to {@link
     * #render}.
     */
    public String renderPlain(String template, Map<String, Object> variables) {
        return substitute(template, variables, false);
    }

    /**
     * Apply {@code {{varName}}} substitution. {@code escapeHtml=true}
     * escapes the substituted value unless the placeholder uses the
     * {@code {{!varName}}} raw-escape form. Missing variables substitute
     * to empty string + emit a warn log so they're observable.
     */
    private String substitute(String template, Map<String, Object> variables, boolean escapeHtml) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuilder out = new StringBuilder(template.length() + 64);
        while (matcher.find()) {
            boolean raw = matcher.group(1) != null;
            String name = matcher.group(2);
            Object rawValue = variables.get(name);
            String value;
            if (rawValue == null) {
                if (!variables.containsKey(name)) {
                    log.warn(
                            "Email template variable '{}' has no value supplied — rendering as empty string",
                            name);
                }
                value = "";
            } else {
                value = String.valueOf(rawValue);
            }
            if (escapeHtml && !raw) {
                value = escapeHtml(value);
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
