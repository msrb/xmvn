/*-
 * Copyright (c) 2013 Red Hat, Inc.
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
package org.fedoraproject.maven.installer.old;

import java.io.IOException;
import java.nio.file.Path;

import org.fedoraproject.maven.config.PackagingRule;

/**
 * @author Mikolaj Izdebski
 */
public interface Package
{
    FragmentFile getMetadata();

    void createDepmaps( String groupId, String artifactId, String version, Path jppGroup, Path jppName,
                        PackagingRule rule )
        throws IOException;
}