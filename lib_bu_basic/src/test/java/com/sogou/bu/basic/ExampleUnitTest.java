package com.sogou.bu.basic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 * @author yangfeng
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void additionTest() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testReverseBetween() {
        int[] exp = {1, 4, 3, 2, 5};
        int[] a = {1, 2, 3, 4, 5};
        ListNode head = buildList(a);
        ListNode r = new Solution().reverseBetween(head, 2, 4);
        assertList(r, exp);
    }

    private static ListNode buildList(int[] a) {
        if (null == a || a.length < 1) {
            return null;
        }

        ListNode head = new ListNode(a[0]);
        ListNode cur = head;
        for (int i = 1; i < a.length; i++) {
            cur.next = new ListNode(a[i]);
            cur = cur.next;
        }
        return head;
    }

    private void assertList(ListNode r, int[] exp) {
        ListNode cur = r;
        for (int i = 0; i < exp.length; i++) {
            assertEquals("The items of both arrays should be equals with i = " + i, exp[i], cur.val);
            cur = cur.next;
        }
        assertNull("It finally reach the end of the list.", cur);
    }

    /** 203. Remove Linked List Elements
     * Remove all elements from a linked list of integers that have value val.
     * Input:  1->2->6->3->4->5->6, val = 6
     * Output: 1->2->3->4->5
     */
    @Test
    public void removeElementsTest() {
        int[] exp = {1, 2, 3, 4, 5};
        int[] a = {1, 2, 6, 3, 4, 5, 6};
        ListNode head = buildList(a);
        ListNode r = new Solution().removeElements(head, 6);
        assertList(r, exp);
    }

    /**
     * 27. Remove Element
     */
    @Test
    public void removeElementTest() {
        int[] a = new int[] {3,2,2,3};
        int[] b = new int[] {0,1,2,2,3,0,4,2};
        assertEquals(2, new Solution().removeElement(a, 3));
        assertEquals(5, new Solution().removeElement(b, 2));

        int[] ra = {2, 2};
        int[] rb = {0, 1, 3, 0, 4};
        assertArray(ra, a);
        assertArray(rb, b);
    }

    private void assertArray(int[] rb, int[] b) {
        if (null != rb && null != b) {
            int l = Math.min(rb.length, b.length);
            for (int i = 0; i < l; i++) {
                assertEquals("The elements of both arrays should be equal.", rb[i], b[i]);
            }
        }
    }
}
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
class Solution {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode left = null;
        ListNode cur = head;
        int i = 1;
        while(i++ < m) {
            left = cur;
            cur = cur.next;
        }
        ListNode tail = cur;  // node m

        ListNode prev = null;
        while (i <= n + 1) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
            i++;
        }

        if (null != tail) {
            tail.next = cur;
        }

        if (null != left && null != prev) {
            left.next = prev;
            return head;
        } else {
            return prev;
        }
    }

    /**
     * 203. Remove Linked List Elements
     * @param head
     * @param val
     * @return
     */
    public ListNode removeElements(ListNode head, int val) {
        while(null != head && head.val == val) {
            head = head.next;
        }

        if (null != head) {
            // assert head.val != val
            ListNode prev = head;
            ListNode cur = head.next;
            boolean deleting = false;
            while(null != cur) {
                if (cur.val != val) {
                    if (deleting) {
                        prev.next = cur;
                        deleting = false;
                    }
                    prev = cur;
                } else {
                    deleting = true;
                }
                cur = cur.next;
            }
            if (deleting) {
                prev.next = null;
//                deleting = false;
            }
            return head;
        }

        return null;
    }

    public ListNode removeDuplicateElements(ListNode head) {
        if (null != head) {
            ListNode prev = head;
            ListNode cur = head;
            int last = cur.val;
            while(null != cur.next) {
                if (cur.val != last) {
                    last = cur.val;
                    prev.next = cur;
                    prev = cur;
                }
                cur = cur.next;
            }
        }

        return null;
    }

    /**
     * 27. Remove Element
     * @param nums
     * @param val
     * @return
     */
    public int removeElement(int[] nums, int val) {
        int count = 0;
        if (null != nums) {
            for (int k = 0; k < nums.length; k++) {
                if (nums[k] != val) {
                    nums[count++] = nums[k];
                }
            }
        }
        return count;
    }
}