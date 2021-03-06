/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.domain;

import org.hibernate.internal.util.Value;
import org.hibernate.service.classloading.spi.ClassLoaderService;

/**
 * Models the naming of a Java type where we may not have access to that type's {@link Class} reference.  Generally
 * speaking this is the case in various hibernate-tools and reverse-engineering use cases.
 *
 * @author Steve Ebersole
 */
public class JavaType {
	private final String name;
	private final Value<Class<?>> classReference;

	public JavaType(final String name, final ClassLoaderService classLoaderService) {
		this.name = name;
		this.classReference = new Value<Class<?>>(
				new Value.DeferredInitializer<Class<?>>() {
					@Override
					public Class<?> initialize() {
						return classLoaderService.classForName( name );
					}
				}
		);
	}

	public JavaType(Class<?> theClass) {
		this.name = theClass.getName();
		this.classReference = new Value<Class<?>>( theClass );
	}

	public String getName() {
		return name;
	}

	public Class<?> getClassReference() {
		return classReference.getValue();
	}

	@Override
	public String toString() {
		return new StringBuilder( super.toString() )
				.append( "[name=" ).append( name ).append( "]" )
				.toString();
	}
}
