/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Sun Microsystems, Inc. nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.javacc.jjtree;

import org.javacc.parser.JavaCCGlobals;
import org.javacc.parser.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

public final class IO {

  private String            ifn;
  private String            ofn;
  private Reader            in;
  private PrintWriter       out;
  private final PrintStream msg;
  private final PrintStream err;

  public IO() {
    ifn = "<uninitialized input>";
    msg = System.out;
    err = System.err;
  }

  public String getInputFileName() {
    return ifn;
  }

  public Reader getIn() {
    return in;
  }

  public String getOutputFileName() {
    return ofn;
  }

  public PrintWriter getOut() {
    return out;
  }

  public PrintStream getMsg() {
    return msg;
  }

  public PrintStream getErr() {
    return err;
  }


  public void print(String s) {
    out.print(s);
  }

  public void println(String s) {
    out.print(s);
    out.println();
  }

  public void println() {
    out.println();
  }


  public void closeAll() {
    if (out != null) {
      out.close();
    }
    if (msg != null) {
      msg.flush();
    }
    if (err != null) {
      err.flush();
    }
  }


  private String create_output_file_name(String i, JJTreeContext context) {
    String o = context.treeOptions().getOutputFile();

    if (o.equals("")) {
      int s = i.lastIndexOf(File.separatorChar);
      if (s >= 0) {
        i = i.substring(s + 1);
      }

      int di = i.lastIndexOf('.');
      if (di == -1) {
        o = i + ".jj";
      } else {
        String suffix = i.substring(di);
        if (suffix.equals(".jj")) {
          o = i + ".jj";
        } else {
          o = i.substring(0, di) + ".jj";
        }
      }
    }

    return o;
  }


  public void setInput(String fn) throws JJTreeIOException {
    try {
      File fp = new File(fn);
      if (!fp.exists()) {
        throw new JJTreeIOException("File " + fn + " not found.");
      }
      if (fp.isDirectory()) {
        throw new JJTreeIOException(fn + " is a directory. Please use a valid file name.");
      }
      if (org.javacc.parser.JavaCCGlobals.isGeneratedBy("JJTree", fn)) {
        throw new JJTreeIOException(fn + " was generated by jjtree.  Cannot run jjtree again.");
      }
      ifn = fp.getPath();

      in = new BufferedReader(new InputStreamReader(new FileInputStream(ifn), Options.getGrammarEncoding()));

    } catch (NullPointerException ne) { // Should never happen
      throw new JJTreeIOException(ne.toString());
    } catch (SecurityException se) {
      throw new JJTreeIOException("Security violation while trying to open " + fn);
    } catch (FileNotFoundException e) {
      throw new JJTreeIOException("File " + fn + " not found.");
    } catch (IOException ioe) {
      throw new JJTreeIOException(ioe.toString());
    }
  }

  void setOutput(JJTreeContext context) throws JJTreeIOException {
    try {
      context.createOutputDir(context.treeOptions().getJJTreeOutputDirectory());
      File ofile = new File(context.treeOptions().getJJTreeOutputDirectory(), create_output_file_name(ifn, context));
      ofn = ofile.toString();
      out = new PrintWriter(new FileWriter(ofile));
    } catch (IOException ioe) {
      throw new JJTreeIOException("Can't create output file " + ofn);
    }
  }
}

/* end */
