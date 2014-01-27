/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.console;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

public class ImportUsagePoint {

    public static void upload(String filename, String url, HttpClient client) throws IOException {
        HttpPost post = new HttpPost(url);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        File file = new File(filename);
        entity.addPart("file", new FileBody(((File) file), "application/rss+xml"));

        post.setEntity(entity);

        client.execute(post);
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                String filename = args[0];
                String url = args[1];

                DefaultHttpClient client = new DefaultHttpClient();
                client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                upload(filename, url, client);
                client.getConnectionManager().shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: import_usage_point.sh filename url");
            System.out.println("");
            System.out.println("Example:");
            System.out.println("");
            System.out.println("  import_usage_point.sh etc/usage_point.xml http://localhost:8080/custodian/retailcustomers/1/upload");
        }
    }
}
