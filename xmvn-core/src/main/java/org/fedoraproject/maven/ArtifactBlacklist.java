/*-
 * Copyright (c) 2012-2013 Red Hat, Inc.
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
package org.fedoraproject.maven;

import java.util.Set;
import java.util.TreeSet;

import org.fedoraproject.maven.model.Artifact;

public class ArtifactBlacklist
{
    private static final Set<Artifact> blacklist = new TreeSet<>();

    public static boolean contains( String groupId, String artifactId )
    {
        return contains( new Artifact( groupId, artifactId ) );
    }

    public static boolean contains( Artifact artifact )
    {
        return blacklist.contains( artifact.clearVersionAndExtension() );
    }

    public static void add( String groupId, String artifactId )
    {
        add( new Artifact( groupId, artifactId ) );
    }

    public static void add( Artifact artifact )
    {
        blacklist.add( artifact.clearVersionAndExtension() );
    }

    static
    {
        add( Artifact.DUMMY );
        add( Artifact.DUMMY_JPP );

        // TODO: This list should be configurable somehow
        add( "javax.activation", "activation" );
        add( "org.eclipse.jetty.orbit", "javax.activation" );
        add( "org.apache.maven.wagon", "wagon-webdav" );
        add( "org.apache.maven.wagon", "wagon-webdav-jackrabbit" );
    }
}
