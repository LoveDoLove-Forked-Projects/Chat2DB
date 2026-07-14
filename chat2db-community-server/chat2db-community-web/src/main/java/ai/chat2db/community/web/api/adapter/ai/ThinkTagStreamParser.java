package ai.chat2db.community.web.api.adapter.ai;

/**
 * Streaming parser for {@code <think>...</think>} tags.
 *
 * <p>Some model deployments do not return reasoning in a separate
 * {@code reasoning_content} field. Instead, they embed it in content using
 * {@code <think>} tags. This parser splits streaming content into reasoning
 * and answer sections as a fallback when {@code reasoning_content} is absent.
 *
 * <p>A {@code <think>} tag is treated as reasoning only before answer content,
 * when the preceding content contains whitespace or reasoning blocks only.
 * After non-whitespace answer content starts, later tags are passed through
 * literally.
 *
 * <p>A tag may be split across chunks, so a possible trailing tag prefix is
 * buffered until a later chunk disambiguates it. Call {@link #flush()} at the
 * end of the stream to retrieve any remaining text.
 */
public class ThinkTagStreamParser {

    private static final String OPEN_TAG = "<think>";
    private static final String CLOSE_TAG = "</think>";

    private final StringBuilder pending = new StringBuilder();
    private boolean insideThink;
    private boolean answerStarted;

    public record Segments(String reasoning, String answer) {
    }

    public Segments consume(String delta) {
        if (delta == null || delta.isEmpty()) {
            return new Segments("", "");
        }
        if (answerStarted) {
            return new Segments("", delta);
        }
        pending.append(delta);
        StringBuilder reasoning = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        while (true) {
            if (insideThink) {
                int closeIndex = pending.indexOf(CLOSE_TAG);
                if (closeIndex >= 0) {
                    reasoning.append(pending, 0, closeIndex);
                    pending.delete(0, closeIndex + CLOSE_TAG.length());
                    insideThink = false;
                    continue;
                }
                emitAllButPartialSuffix(CLOSE_TAG, reasoning);
                break;
            }

            int openIndex = pending.indexOf(OPEN_TAG);
            if (openIndex >= 0) {
                if (isBlank(pending, openIndex)) {
                    answer.append(pending, 0, openIndex);
                    pending.delete(0, openIndex + OPEN_TAG.length());
                    insideThink = true;
                    continue;
                }
                startAnswer(answer);
                break;
            }

            int hold = partialTagSuffixLength(OPEN_TAG);
            if (hasNonBlank(pending, pending.length() - hold)) {
                startAnswer(answer);
            } else {
                emitAllButPartialSuffix(OPEN_TAG, answer);
            }
            break;
        }
        return new Segments(reasoning.toString(), answer.toString());
    }

    public Segments flush() {
        String rest = pending.toString();
        pending.setLength(0);
        if (insideThink) {
            insideThink = false;
            return new Segments(rest, "");
        }
        return new Segments("", rest);
    }

    private void startAnswer(StringBuilder answer) {
        answer.append(pending);
        pending.setLength(0);
        answerStarted = true;
    }

    private void emitAllButPartialSuffix(String tag, StringBuilder out) {
        int hold = partialTagSuffixLength(tag);
        int emit = pending.length() - hold;
        if (emit > 0) {
            out.append(pending, 0, emit);
            pending.delete(0, emit);
        }
    }

    private int partialTagSuffixLength(String tag) {
        int max = Math.min(pending.length(), tag.length() - 1);
        for (int len = max; len > 0; len--) {
            if (suffixMatchesTagPrefix(len, tag)) {
                return len;
            }
        }
        return 0;
    }

    private boolean suffixMatchesTagPrefix(int len, String tag) {
        int offset = pending.length() - len;
        for (int i = 0; i < len; i++) {
            if (pending.charAt(offset + i) != tag.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isBlank(CharSequence text, int endExclusive) {
        return !hasNonBlank(text, endExclusive);
    }

    private static boolean hasNonBlank(CharSequence text, int endExclusive) {
        for (int i = 0; i < endExclusive; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
