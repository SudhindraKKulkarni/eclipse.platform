/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * The job manager provides facilities for scheduling, querying, and maintaining jobs
 * and locks.  In particular, the job manager provides the following services:
 * <ul>
 * <li>Maintains a queue of jobs that are waiting to be run.  Items can be added to
 * the queue using the <code>schedule</code> method.</li>
 * <li>Allows manipulation of groups of jobs called job families.  Job families can
 * be canceled, put to sleep, or woken up atomically.  There is also a mechanism
 * for querying the set of known jobs in a given family.</li>
 * <li>Allows listeners to find out about progress on running jobs, and to find out
 * when jobs have changed states.</li>
 * <li>Provides a factory for creating lock objects.  Lock objects are smart monitors
 * that have strategies for avoiding deadlock.</li>
 * <li>Provide feedback to a client that is waiting for a given job or family of jobs
 * to complete.</li>
 * </ul>
 * 
 * @see Job
 * @see ILock
 * @since 3.0
 */
public interface IJobManager {
	/**
	 * Registers a job listener with the job manager.  
	 * Has no effect if an identical listener is already registered.
	 * 
	 * @param listener the listener to be added.
	 */
	public void addJobChangeListener(IJobChangeListener listener);
	/**
	 * Begins applying this rule in the calling thread.  If the rule conficts with another
	 * rule currently running in another thread, this method blocks until there are
	 * no conflicting rules.  Calls to <tt>beginRule</tt> must eventually be followed
	 * by a matching call to <tt>endRule</tt> in the same thread and with the identical
	 * rule instance. 
	 * <p>
	 * Rules can be nested only if the rule for the inner <tt>beginRule</tt>
	 * is contained within the rule for the outer <tt>beginRule</tt>.  Rule containment
	 * is tested with the API method <tt>ISchedulingRule.contains</tt>.  Also, begin/end
	 * pairs must be strictly nested.  Only the rule that has most recently begun
	 * can be ended at any given time.
	 * <p>
	 * A rule of <code>null</code> can be used, but will be ignored for scheduling 
	 * purposes.  The outermost non-null rule in the thread will be used for scheduling. A
	 * <code>null</code> rule that is begun must still be ended.
	 * <p>
	 * If this method is called from within a job that has a scheduling rule, the
	 * given rule must also be contained within the rule for the running job.
	 * <p>
	 * Note that <tt>endRule</tt> must be called even if <tt>beginRule</tt> fails.
	 * The recommended usage is:
	 * <pre>
	 * final ISchedulingRule rule = ...;
	 * try {
	 * 	manager.beginRule(rule, monitor);
	 * } finally {
	 * 	manager.endRule(rule);
	 * }
	 * </pre>
	 * <p>
	 * This API is experimental, and is subject to change or removal without notice.
	 * 
	 * @param rule the rule to begin applying in this thread, or <code>null</code>
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws IllegalArgumentException if the rule is not strictly nested within
	 * all other rules currently active for this thread.
	 * @throws OperationCanceledException if the supplied monitor reports cancelation
	 * 	before the rule becomes available.
	 * @see ISchedulingRule.contains
	 */
	public void beginRule(ISchedulingRule rule, IProgressMonitor monitor);
	/**
	 * @deprecated use beginRule(ISchedulingRule, IProgressMonitor)
	 */
	public void beginRule(ISchedulingRule rule);
	/**
	 * Cancels all jobs in the given job family.  Jobs in the family that are currently waiting
	 * will be removed from the queue.  Sleeping jobs will be discarded without having 
	 * a chance to wake up.  Currently executing jobs will be asked to cancel but there 
	 * is no guarantee that they will do so.
	 * 
	 * @param family the job family to cancel, or <code>null</code> to cancel all jobs.
	 * @see Job#belongsTo(Object)
	 */
	public void cancel(Object family);
	/**
	 * Returns the job that is currently running in this thread, or null if there
	 * is no currently running job.
	 */
	public Job currentJob();
	/**
	 * Ends the application of a rule to the calling thread.  Calls to <tt>endRule</tt> 
	 * must be preceded by a matching call to <tt>beginRule</tt> in the same thread
	 * with an identical rule instance.
	 * <p>
	 * Rules can be nested only if the rule for the inner <tt>beginRule</tt>
	 * is contained within the rule for the outer <tt>beginRule</tt>.  Also, begin/end
	 * pairs must be strictly nested.  Only the rule that has most recently begun
	 * can be ended at any given time.
	 * <p>
	 * This API is experimental, and is subject to change or removal without notice.
	 * 
	 * @param rule the rule to end applying in this thread
	 * @throws IllegalArgumentException if this method is called on a rule for which
	 * there is no matching begin, or that does not match the most recent begin.
	 * @see ISchedulingRule#contains
	 */
	public void endRule(ISchedulingRule rule);
	/**
	 * Returns all waiting, executing and sleeping jobs belonging
	 * to the given family. If no jobs are found, an empty array is returned.
	 * 
	 * @param family the job family to find, or <code>null</code> to find all jobs.
	 * @see Job#belongsTo(Object)
	 */
	public Job[] find(Object family);
	/**
	 * Waits until all jobs of the given family are finished.  This method will block the 
	 * calling thread until all such jobs have finished executing, or until this thread is
	 * interrupted.   If there are no jobs in 
	 * the family that are currently waiting, running, or sleeping, this method returns 
	 * immediately.  Feedback on how the join is progressing is provided to a  progress 
	 * monitor.
	 * 
	 * <p>
	 * If the calling thread owns any locks, the locks may be released during the
	 * join if necessary to prevent deadlock.  On return from the join, the calling
	 * thread will once again have exclusive control of any locks that were owned
	 * upon entering the join.
	 * </p>
	 * <p>
	 * Warning: this method can result in starvation of the current thread if
	 * another thread continues to add jobs of the given family.
	 * </p>
	 * 
	 * @param family the job family to join, or <code>null</code> to join all jobs.
	 * @param monitor Progress monitor for reporting progress on how the
	 * wait is progressing, or <code>null</code> if no progress monitoring is required.
	 * @exception InterruptedException if this thread is interrupted while waiting
	 * @exception OperationCanceledException if the progress monitor is canceled while waiting
	 * @see Job#belongsTo(Object)
	 */
	public void join(Object family, IProgressMonitor monitor) throws InterruptedException, OperationCanceledException;
	/**
	 * Creates a new lock object.  All lock objects supplied by the job manager
	 * know about each other and will always avoid circular deadlock amongst
	 * themselves.
	 * 
	 * @return the new lock object
	 */
	public ILock newLock();
	/**
	 * Removes a job listener from the job manager.  
	 * Has no effect if an identical listener is not already registered.
	 * 
	 * @param listener the listener to be removed.
	 */
	public void removeJobChangeListener(IJobChangeListener listener);
	/**
	 * Provides a hook that is notified whenever a thread is about to wait on a lock,
	 * or when a thread is about to release a lock.  This hook must only be set once.
	 * <p>
	 * This method is for internal use by the platform-related plug-ins.  
	 * Clients should not call this method.
	 * </p>
	 * @see LockListener
	 */
	public void setLockListener(LockListener listener);
	/**
	 * Registers a progress provider with the job manager.  If there was a
	 * provider already registered, it is replaced.
	 * <p>
	 * This method is for internal use by the platform-related plug-ins.  
	 * Clients should not call this method.
	 * </p>
	 * 
	 * @param provider the new provider, or <code>null</code> if no progress
	 * is needed.
	 */
	public void setProgressProvider(IProgressProvider provider);
	/**
	 * Requests that all jobs in the given job family be suspended.  Jobs currently 
	 * waiting to be run will be removed from the queue and moved into the 
	 * <code>SLEEPING</code> state.  Jobs that have been put to sleep
	 * will remain in that state until either resumed or canceled.  This method has
	 * no effect on jobs that are not currently waiting to be run.
	 * 
	 * Sleeping jobs can be resumed using <code>wakeUp</code>.
	 * 
	 * @param family the job family to sleep, or <code>null</code> to sleep all jobs.
	 * @see Job#belongsTo(Object)
	 */
	public void sleep(Object family);
	/**
	 * Resumes scheduling of all sleeping jobs in the given family.  This method
	 * has no effect on jobs in the family that are not currently sleeping.
	 * 
	 * @param family the job family to wake up, or <code>null</code> to wake up all jobs.
	 * @see Job#belongsTo(Object)
	 */
	public void wakeUp(Object family);
}