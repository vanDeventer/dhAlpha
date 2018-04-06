/*
 */
package com.aaa_ngac_demo.AAA_NGAC_Server.ngac_database;

/**
 *
 * @author cripan-local
 */
public class assignment {
    private int id;
    private int start_node_id;
    private int end_node_id;
    private int depth;
    private int assignment_path_id;
    
    	public assignment(){}
	
	public assignment(int start_node_id,int end_node_id,int depth,int assignment_path_id) {
		super();
                this.start_node_id=start_node_id;
                this.end_node_id=end_node_id;
		this.depth = depth;
		this.assignment_path_id = assignment_path_id;

	}

    public int getId() {
        return id;
    }

    public int getStart_node_id() {
        return start_node_id;
    }

    public int getEnd_node_id() {
        return end_node_id;
    }

    public int getDepth() {
        return depth;
    }

    public int getAssignment_path_id() {
        return assignment_path_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStart_node_id(int start_node_id) {
        this.start_node_id = start_node_id;
    }

    public void setEnd_node_id(int end_node_id) {
        this.end_node_id = end_node_id;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setAssignment_path_id(int assignment_path_id) {
        this.assignment_path_id = assignment_path_id;
    }
}
