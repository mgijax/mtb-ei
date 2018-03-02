/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/actions/LoginAction.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.actions;

import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;

/**
 * Authenticates the user in the MTB system.
 *
 * @author mjv
 * @date 2007/04/30 15:50:39
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/actions/LoginAction.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 */
public class LoginAction {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables
    // none

    private MTBUsersDTO dtoResult = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new instance of LoginAction
     */
    public LoginAction() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Attempt to login to the MTB System with the supplied user id and
     * password.
     *
     * @param user the user id
     * @param password the password
     * @return <code>true</code> if the user logged in successfully,
     *         <code>false</code> otherwise
     */
    public boolean login(String user, String password)
        throws Exception {

        boolean ret = false;

        // get the DAO
        MTBUsersDAO dao = MTBUsersDAO.getInstance();

        // create a DTO for user name only to make sure the user exists
        MTBUsersDTO dtoAttempt = dao.createMTBUsersDTO();
        dtoAttempt.setUserName(user);

        
        // perform the lookup
        dtoResult = dao.loadUniqueUsingTemplate(dtoAttempt);
        

        // dtoResult will be null if the user does not exist
        if ((dtoResult != null) && dtoResult.getUserName().equals(user) &&
            dtoResult.getPassword().equals(password)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Get the authenticated <code>MTBUsersDTO</code> obeject.
     *
     * @return the authenticated <code>MTBUsersDTO</code> obeject
     */
    public final MTBUsersDTO getUser() {
        return dtoResult;
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
