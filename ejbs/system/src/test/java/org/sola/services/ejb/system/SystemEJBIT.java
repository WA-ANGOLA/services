/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.services.ejb.system;

import java.util.Calendar;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.admin.services.ejb.system.businesslogic.SystemAdminEJB;
import org.sola.admin.services.ejb.system.businesslogic.SystemAdminEJBLocal;
import org.sola.admin.services.ejb.system.repository.entities.Br;
import org.sola.admin.services.ejb.system.repository.entities.BrValidation;
import static org.junit.Assert.*;
import org.sola.services.common.EntityAction;
import org.sola.admin.services.ejb.system.repository.entities.EmailTask;

/**
 *
 * @author manoku
 */
public class SystemEJBIT extends AbstractEJBTest {

    private static final String LOGIN_USER = "test";
    private static final String LOGIN_PASS = "test";

    public SystemEJBIT() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        login(LOGIN_USER, LOGIN_PASS);
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

    @Test
    @Ignore
    public void testEmailTasks() throws Exception {
        SystemAdminEJBLocal instance = (SystemAdminEJBLocal) getEJBInstance(SystemAdminEJB.class.getSimpleName());
        // Create new email task
        EmailTask t = new EmailTask();
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            t.setId("t1");
            t.setAttachmentName("test.txt");
            t.setAttachment(new byte[]{1, 2, 3, 4});
            t.setAttempt(1);
            t.setBcc("hidden@email.com");
            t.setBody("Message body");
            t.setCc("copy@mail.com");
            t.setSubject("message subject");
            t.setTimeToSend(Calendar.getInstance().getTime());
            t.setRecipient("recipient@email.com");

            instance.saveEmailTask(t);

            // Get task
            t = null;
            List<EmailTask> tasks = instance.getEmailsToSend();
            assertNotNull("Email tasks not found", tasks);
            System.out.println(">>> Found emails - " + tasks.size());
            
            for (EmailTask emailTask : tasks) {
                if(emailTask.getId().equals("t1")){
                    t = emailTask;
                    System.out.println(">>> Created email found");
                    break;
                }
            }
            assertNotNull("Email tasks was not found in the list of emails to send now", t);
            
            // Try to update
            t.setSubject("new");
            t.setEntityAction(EntityAction.UPDATE);
            instance.saveEmailTask(t);
            
            t = instance.getEmailTask("t1");
            assertNotNull("Email tasks \"t1\" was not found", t);
            assertEquals("Email tasks \"t1\" was not updated properly", t.getSubject(), "new");
            System.out.println(">>> Email task \"t1\" was successfully updated with subject = new");
            
            // Try to delete
            t.setEntityAction(EntityAction.DELETE);
            instance.saveEmailTask(t);
            t = instance.getEmailTask("t1");
            assertNull("Email tasks \"t1\" was not deleted", t);
            System.out.println(">>> Email task \"t1\" was successfully deleted");
            
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    @Test
    @Ignore
    public void getBr() throws Exception {
        SystemAdminEJBLocal instance = (SystemAdminEJBLocal) getEJBInstance(SystemAdminEJB.class.getSimpleName());
        Br br = instance.getBr("app-shares-total-check", null);
        assertNotNull("Can't find Business Rule \"app-shares-total-check\"", br);
        System.out.println(">>> Found business rule with feedback \"" + br.getFeedback() + "\"");
    }

    @Test
    @Ignore
    public void saveBr() throws Exception {
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            SystemAdminEJBLocal instance = (SystemAdminEJBLocal) getEJBInstance(SystemAdminEJB.class.getSimpleName());
            Br br = instance.getBr("app-shares-total-check", null);
            assertNotNull("Can't find Business Rule \"app-shares-total-check\"", br);
            System.out.println(">>> Found business rule with feedback \"" + br.getFeedback() + "\"");

            Br savedBr = instance.saveBr(br);
            assertNotNull("Can't save Business Rule \"app-shares-total-check\"", savedBr);
            System.out.println(">>> Business rule \"app-shares-total-check\" successfully saved");
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    private void printResult(List<BrValidation> result) {
        System.out.println("Found: " + (result == null ? "!None!" : result.size()));
    }
}
