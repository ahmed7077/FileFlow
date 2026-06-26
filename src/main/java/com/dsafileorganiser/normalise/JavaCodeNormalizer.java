package com.dsafileorganiser.normalise;

import java.util.stream.Collectors;

public final class JavaCodeNormalizer {
    public String normalize(String code, boolean removeComments) {
        String normalizedLineEndings = code.replace("\r\n", "\n").replace('\r', '\n');
        String preparedCode = removeComments ? stripComments(normalizedLineEndings) : normalizedLineEndings;

        return preparedCode
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.replaceAll("\\s+", " "))
                .collect(Collectors.joining("\n"))
                .trim();
    }

    public String stripComments(String code) {
        StringBuilder builder = new StringBuilder(code.length());
        boolean insideString = false;
        boolean insideCharacter = false;
        boolean insideLineComment = false;
        boolean insideBlockComment = false;
        boolean escaped = false;

        for (int index = 0; index < code.length(); index++) {
            char current = code.charAt(index);
            char next = index + 1 < code.length() ? code.charAt(index + 1) : '\0';

            if (insideLineComment) {
                if (current == '\n') {
                    builder.append(current);
                    insideLineComment = false;
                }
                continue;
            }

            if (insideBlockComment) {
                if (current == '*' && next == '/') {
                    insideBlockComment = false;
                    index++;
                } else if (current == '\n') {
                    builder.append(current);
                }
                continue;
            }

            if (insideString) {
                builder.append(current);
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    insideString = false;
                }
                continue;
            }

            if (insideCharacter) {
                builder.append(current);
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '\'') {
                    insideCharacter = false;
                }
                continue;
            }

            if (current == '/' && next == '/') {
                insideLineComment = true;
                index++;
                continue;
            }

            if (current == '/' && next == '*') {
                insideBlockComment = true;
                index++;
                continue;
            }

            if (current == '"') {
                insideString = true;
            } else if (current == '\'') {
                insideCharacter = true;
            }

            builder.append(current);
        }

        return builder.toString();
    }
}
