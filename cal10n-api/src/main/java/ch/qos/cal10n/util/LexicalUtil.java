/*
 * Copyright (c) 2009 QOS.ch All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS  IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ch.qos.cal10n.util;

/**
 * @author Ceki G&uuml;c&uuml;
 */
public class LexicalUtil {


  public static StringBuilder convertSpecialCharacters(StringBuilder inBuf) {

    int i = inBuf.indexOf("\\");

    if (i == -1) {
      return inBuf;
    }

    StringBuilder outBuf = new StringBuilder(inBuf.length());

    int followIndex = -1;  // index of char following the latest backslash
    while (i != -1) {
      outBuf.append(inBuf.subSequence(followIndex + 1, i));
      followIndex = i + 1;
      char c = inBuf.charAt(followIndex);
      switch (c) {
        case 'u':
          char unicodeChar = readUnicode(inBuf, followIndex + 1);
          outBuf.append(unicodeChar);
          followIndex += 4;
          break;
        case 'n':
          outBuf.append('\n');
          break;
        case 'r':
          outBuf.append('\r');
          break;
        case 't':
          outBuf.append('\t');
          break;
        case 'f':
          outBuf.append('\f');
          break;
        case ':': // see http://tinyurl.com/bprdgnk , i.e. the javadocs for Properties.store()
        case '#':
        case '!':
        case '=':
          outBuf.append(c);
          break;
        default:
          outBuf.append('\\');
          outBuf.append(c);
      }
      // search for the next \
      i = inBuf.indexOf("\\", followIndex + 1);
    }
    outBuf.append(inBuf.subSequence(followIndex + 1, inBuf.length()));
    return outBuf;
  }

  // see http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#95504

  private static char readUnicode(StringBuilder inBuf, int i) {
    int r = 0;
    for (int j = i; j < i + 4; j++) {
      char atJ = inBuf.charAt(j);

      if (atJ >= '0' && atJ <= '9') {
        r = (r << 4) + atJ - '0';
        continue;
      }
      if (atJ >= 'A' && atJ <= 'F') {
        // '7' = 'A' - 10
        r = (r << 4) + atJ - '7';
        continue;
      }
      if (atJ >= 'a' && atJ <= 'f') {
        // 'W' = 'a' - 10
        r = (r << 4) + atJ - 'W';
        continue;
      }
    }
    return (char) r;
  }
}
