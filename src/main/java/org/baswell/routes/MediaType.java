/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.routes;

import java.util.List;

import static java.util.Arrays.*;

/**
 * Media types that Routes is aware of. A media type has one or more mime types and one more file extensions.
 *
 * @see org.baswell.routes.Route#respondsToMedia()
 * @see RequestedMediaType#mediaType
 */

public enum MediaType
{
  ATOM(asList(MIMETypes.ATOM), asList("atomcat")),
  CSV(asList(MIMETypes.CSV), asList("csv")),
  EXCEL(asList(MIMETypes.EXCEL, MIMETypes.EXCEL2, MIMETypes.EXCEL3, MIMETypes.EXCEL4, MIMETypes.EXCEL5, MIMETypes.EXCEL6, MIMETypes.EXCEL7), asList("xlsx", "xls")),
  HTML(asList(MIMETypes.HTML, MIMETypes.HTML2), asList("html", "htm", "htmls", "html")),
  ICS(asList(MIMETypes.ICS), asList("ics")),
  JAVASCRIPT(asList(MIMETypes.JAVASCRIPT, MIMETypes.JAVASCRIPT2, MIMETypes.JAVASCRIPT3), asList("js")),
  JSON(asList(MIMETypes.JSON, MIMETypes.JSON2), asList("json")),
  PDF(asList(MIMETypes.PDF), asList("pdf")),
  RSS(asList(MIMETypes.RSS), asList("rss")),
  TEXT(asList(MIMETypes.TEXT), asList("text", "txt")),
  WORD(asList(MIMETypes.WORD, MIMETypes.WORD2), asList("doc", "docx")),
  XML(asList(MIMETypes.XML, MIMETypes.XML2), asList("xml"));

  /**
   * The default mime type.
   */
  public final String mimeType;

  /**
   * All mime types.
   */
  public final List<String> mimeTypes;

  /**
   * The default file extension.
   */
  public final String extension;

  /**
   * All file extensions.
   */
  public final List<String> extensions;

  /**
   *
   * @param mimeType The mime type to look for
   * @return The {@code MediaType} that has a matching mime type or {@code null}.
   */
  public static MediaType findFromMimeType(String mimeType)
  {
    if (mimeType != null)
    {
      mimeType = mimeType.toLowerCase();
      for (MediaType mediaType : values())
      {
        for (String mediaTypeMimeType : mediaType.mimeTypes)
        {
          if (mediaTypeMimeType.equals(mimeType))
          {
            return mediaType;
          }
        }
      }
    }

    return null;
  }

  /**
   *
   * @param extension The file extension
   * @return The {@code MediaType} that has a matching file extension or {@code null}.
   */
  public static MediaType findFromExtension(String extension)
  {
    if (extension != null)
    {
      extension = extension.toLowerCase();
      for (MediaType mediaType : values())
      {
        for (String mediaTypeExtension : mediaType.extensions)
        {
          if (mediaTypeExtension.equals(extension))
          {
            return mediaType;
          }
        }
      }
    }

    return null;
  }

  /**
   *
   * @param formatParamter The format parameter
   * @return The {@code MediaType} that has the same name as the format parameter (ex. format=json) or {@code null}.
   */
  public static MediaType findFromFormatParameter(String formatParamter)
  {
    if (formatParamter != null)
    {
      for (MediaType mediaType : values())
      {
        if (mediaType.toString().equalsIgnoreCase(formatParamter))
        {
          return mediaType;
        }
      }
    }

    return null;
  }

  private MediaType(List<String> mimeTypes, List<String> extensions)
  {
    this.mimeTypes = mimeTypes;
    this.extensions = extensions;

    mimeType = mimeTypes.get(0);
    extension = extensions.get(0);
  }
}
