/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/LVBeanListModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 * Author: mjv
 *
 */
package org.jax.mgi.mtb.ei.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueBeanComparator;

/**
 * A model for <code>JcomboBox</code>s and <code>JList</code>s that encapsulate
 * <code>LabelValueBean</code> data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:48
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/LVBeanListModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 */
public class LVBeanListModel<L,V> extends DefaultListModel 
                             implements ComboBoxModel {

    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables
    
    protected List<LabelValueBean<L,V>> data = null;
    protected Object selectedObject = null;

    // ----------------------------------------------------------- Constructors
    

    /**
     * Constructor
     */
    public LVBeanListModel() {
        this.data = new ArrayList<LabelValueBean<L,V>>();
    }
    
    /**
     * Constructor
     *
     * @param arr the array of data
     */
    public LVBeanListModel(List<LabelValueBean<L,V>> arr) {
        this.data = arr;
        sort();
    }
    
    /**
     * Constructor
     *
     * @param arr the array of data
     * @param sort boolean set to false to prevent sorting on label value
     * Will use exsting list order
     */
    public LVBeanListModel(List<LabelValueBean<L,V>> arr, boolean sort){
        this.data = arr;
        if(sort)sort();
    }
    
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Get the number of elements in the model.
     *
     * @return the number of elements
     */
    public int size() {
        return getSize();
    }

    /**
     * Get the number of elements in the model.
     *
     * @return the number of elements
     */
    public int getSize() {
        return data.size();
    }

    /**
     * Get the element at the specified index.
     *
     * @param index the index
     * @return the element at the specified index
     */
    public LabelValueBean<L,V> getElementAt(int index) {
        if (index >= this.getSize()) {
            return null;
        }

        return data.get(index);
    }

    /**
     * Get the label at the specified index.
     *
     * @param index the index
     * @return the label of the element at the specified index
     */
    public L getLabelAt(int index) {
        if (index >= this.getSize()) {
            return null;
        }
        
        return data.get(index).getLabel();
    }
    
    /**
     * Get the value at the specified index.
     *
     * @param index the index
     * @return the value of the element at the specified index
     */
    public V getValueAt(int index) {
        if (index >= this.getSize()) {
            return null;
        }
        
        return data.get(index).getValue();
    }
    
    /**
     * Add an element at the specified index.
     *
     * @param index the index
     * @param element the element to add
     */
    public void add(int index, LabelValueBean<L,V> element) {
        data.add(index, element);
        sort();
        fireContentsChanged(this, 0, getSize());
    }
    
    /**
     * Add an element to the end of the model.
     *
     * @param element the element to add
     */
    public void addElement(LabelValueBean<L,V> element) {
        data.add(element);
        sort();
        fireContentsChanged(this, 0, getSize());
    }
        
    /**
     * Add a <code>Collection</code> of elements to the end of the model.
     *
     * @param c a <code>Collection</code> of elements to add
     */
    public void addAll(Collection c) {
        data.addAll(c);
        sort();
        fireContentsChanged(this, 0, getSize());
    }
    
    /**
     * Add an array of elements to the end of the model.
     *
     * @param elements an arry of elements to add
     */
    public void addAll(LabelValueBean<L,V> elements[]) {
        Collection c = Arrays.asList(elements);
        data.addAll(c);
        sort();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Remove all elements from the model.
     */
    public void clear() {
        data.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * See if the specified element is in the model or not.
     *
     * @param element the element to check
     * @return <code>true</code> if the element is in the model, 
     *         <code>false</code> otherwise
     */
    public boolean contains(LabelValueBean<L,V> element) {
        return data.contains(element);
    }

    /**
     * Get the first element in the model.
     *
     * @return the first element in the model
     */
    public LabelValueBean<L,V> firstElement() {
        if (data != null) {
            return data.get(0);
        }
        
        return null;
    }

    /**
     * Get the last element in the model.
     *
     * @return the last element in the model
     */
    public LabelValueBean<L,V> lastElement() {
        if (data != null) {
            return data.get(data.size() - 1);
        }
        
        return null;
    }
    
    /**
     * Remove the element at the specified index in the model.
     *
     * @param index the index of the model
     * @return the element at the index of the model
     */
    public LabelValueBean<L,V> remove(int index) {
        if ((index < 0) || (index >= data.size())) {
            return null;
        }
        
        LabelValueBean<L,V> ret = data.get(index);
        data.remove(index);
        sort();
        fireContentsChanged(this, 0, getSize());
        return ret;
    }

    /**
     * Remove the element from the model.
     *
     * @param element the element to remove
     * @return <code>true</code> if the element was removed, <code>false</code>
     *         otherwise
     */
    public boolean removeElement(LabelValueBean<L,V> element) {
        boolean removed = data.remove(element);
        if (removed) {
            sort();
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
    
    /**
     * Get the currently selected item.  Used for <code>JComboBox</code>.
     *
     * @return the currently selected element
     */
    public LabelValueBean<L,V> getSelectedItem() {
        return (LabelValueBean<L,V>)selectedObject;
    }

    /**
     * Set the currently selected item.  Used for <code>JComboBox</code>.
     *
     * @param element the element to be selected
     */
    public void setSelectedItem(Object element) {
        this.selectedObject = element;
        fireContentsChanged(this, -1, -1);
    }

    /**
     * Sort the elements in the model.
     */
    public void sort() {
        Collections.sort(data, new LabelValueBeanComparator<LabelValueBean<L,V>>(LabelValueBeanComparator.TYPE_LABEL));
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
    
}
