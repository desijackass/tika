/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser.jpeg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.image.TiffExtractor;
import org.xml.sax.SAXException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

class JpegExtractor {

    private final Metadata metadata;

    public JpegExtractor(Metadata metadata) {
        this.metadata = metadata;
    }

    public void parse(InputStream stream)
            throws IOException, SAXException, TikaException {
        try {
            com.drew.metadata.Metadata jpegMetadata =
                JpegMetadataReader.readMetadata(stream);

            Iterator<?> directories = jpegMetadata.getDirectoryIterator();
            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                Iterator<?> tags = directory.getTagIterator();

                while (tags.hasNext()) {
                    Tag tag = (Tag)tags.next();
                    metadata.set(tag.getTagName(), tag.getDescription());
                    TiffExtractor.handleCommonImageTags(metadata, tag);
                }
                TiffExtractor.handleGeoImageTags(metadata);
            }
        } catch (JpegProcessingException e) {
            throw new TikaException("Can't read JPEG metadata", e);
        } catch (MetadataException e) {
            throw new TikaException("Can't read JPEG metadata", e);
        }
    }
}
