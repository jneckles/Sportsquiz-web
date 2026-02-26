# Jamar Neckles

# Question Responses
1. When the user completes and submits the login form, the request includes the loginID and loginPassword. So then the authenticateUser() method in TasklistServlet checks that both of these are not empty and then checks to make sure the password is the reverse of the loginId. 
Then if this matches the method returns back a unique internal user id by creating a hashCode for the user through userId.hasCode(). When the user is authenticated, the servlet calls setupSession(authenticatedUser, req)
and this stores the unique internal id in the session from the attribute SESSION_ATTRIB_USER. Then for every request, the servlet will check if this session attribute exists, and if it does
then the servlet will recognize that the user is authenticated and then it'll go past the login screen.

3. When the user first logs in the servlet calls this method: 
private void setupSession(String authenticatedUser, HttpServletRequest req) {
		req.getSession().setAttribute(SESSION_ATTRIB_USER, authenticatedUser);
		req.getSession().setAttribute(SESSION_ATTRIB_TASKLIST,
				TasklistPersistence.getInstance().loadPriorTasks(authenticatedUser));
	}
	And the last line specifically is what loads the user's previously saved tasks. Then the reuturned List<Task> is stored in the session under the SESSION_ATTRIB_TASKLIST attribute.
	So whenever tasks are changed, whether added deleted or edited, the servlet gets this list from the session through the getTasklistFromDSession() method and it's updated. So the session is used to store the user's
	task list as long as the session is active.
	
4. First the user fills out the form to add a new task from the tasklist.jsp and then submits it. The form has a field with the mode MODE_ADD_TASK. In the controller() method in TasklistServlet when MODE_ADD_TASK is noticed then the addTask(req) method is called.
In addTask() the method getTaskFromRequest(req) creates the task instance from the task data in the request and a new Task object is made. Then the method addTaskToTasklist(req, task) gets the task list from the session and adds the new task to that list. 

5. In the tasklist.jsp each task has a edit task button and when it is clicked, the request includes the task's id and the MODE_EDIT_TASK. Then the TaskListservlet's controller() calls setupForEdit(req) and it finds the task in the session by using the findTask() method and populates the 
edit form with the current task. Then the user makes the changes and submits then and the jsp uses the mode MODE_SAVE_UPDATE. After this the servlet calls saveUpdatedTask(req) and it gets the updated task from the req from getTaskFromReuqest() method and it finds the original task in the session,
removes it and then adds the new task.
	 
6. the tasklist.jsp gets the task list from the session attribute SESSION_ATTRIB_TASKLIST and then the jsp iterates through the list and displays each task's information (date, title, description) in a table. The buttons for editing and deleting are also displayed. 

7. The application has a session listener TasklistSessionListener that implements HttpSessionListener and when the session is about to expire, 
sessionDestroyed(HttpSessionEvent) is called. In this method the listener gets the user's internal id and the task list from the session. Then in the TasklistPersistence singleton saveTasks(userId, tasklist) is called and this serializes the 
task list and then it writes it to a file. 

8. When a user logins, after they are authenticated the servlet calls setupSession(authenticatedUser, req). In this method it calls TasklistPersistence.getInstance().loadPriorTasks(authenticatedUser)) and this reads the serialized task from a file. Then the 
loaded List<Task> is stored in the session under the SESSION_ATTRIB_TASKLIST. This then makes the tasks be able to displayed and changed when wanted. 


