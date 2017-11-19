/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.bot.spring.LineListener;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class KitchenSinkApplication {
    static Path downloadedContentDir;
    //@Autowired
    static LineListener listener;
    
    @Autowired
    public void setLineListener(LineListener listener) {
    	System.out.println("------------- trying to find linelistener --------------");
    	KitchenSinkApplication.listener = listener;
    }
    
    public static void main(String[] args) throws IOException {
    	//LineListener listener = new LineListener();
    	System.out.println("------------- in main --------------");
    	listener.start();
        downloadedContentDir = Files.createTempDirectory("line-bot");
        SpringApplication.run(KitchenSinkApplication.class, args);
    }
    


}
