/*
 * Copyright 2017 Mario Contreras <marioc@nazul.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.iteso.msc.myresolutions.mx.iteso.msc.myresolutions.utility;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by Mario_Contreras on 5/1/2017.
 */

public class CloudStorage {
    public static AWSCredentialsProvider getAwsCredProvider() {
        return new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                AWSCredentials credentials = new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return "AKIAIEAJLI6GJI7ADBCQ";
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return "zbCGNRjdeiJj8DIZvzlz/6z/WJgG5q9MXnHcsugL";
                    }
                };
                return credentials;
            }

            @Override
            public void refresh() {

            }
        };
    }

    public static AmazonS3Client getS3Client() {
        AmazonS3Client client;
        client = new AmazonS3Client(getAwsCredProvider());
        client.setRegion(Region.getRegion(Regions.US_EAST_1));
        return client;
    }
}

// EOF
