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
package org.hibernate.envers.event;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.synchronization.AuditProcess;
import org.hibernate.envers.synchronization.work.AddWorkUnit;
import org.hibernate.envers.synchronization.work.AuditWorkUnit;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

/**
 * @author Adam Warski (adam at warski dot org)
 * @author HernпїЅn Chanfreau
 * @author Steve Ebersole
 */
public class EnversPostInsertEventListenerImpl extends BaseEnversEventListener implements PostInsertEventListener {
	public EnversPostInsertEventListenerImpl(AuditConfiguration enversConfiguration) {
		super( enversConfiguration );
	}

    public void onPostInsert(PostInsertEvent event) {
        String entityName = event.getPersister().getEntityName();

        if ( getAuditConfiguration().getEntCfg().isVersioned( entityName ) ) {
            AuditProcess auditProcess = getAuditConfiguration().getSyncManager().get(event.getSession());

            AuditWorkUnit workUnit = new AddWorkUnit(
					event.getSession(),
					event.getPersister().getEntityName(),
					getAuditConfiguration(),
                    event.getId(),
					event.getPersister(),
					event.getState()
			);
            auditProcess.addWorkUnit( workUnit );

            if ( workUnit.containsWork() ) {
                generateBidirectionalCollectionChangeWorkUnits(
						auditProcess,
						event.getPersister(),
						entityName,
						event.getState(),
                        null,
						event.getSession()
				);
            }
        }
	}
}
