enum Color {RED, BLACK};

class Node {
    public int data;
    public Color color;
    public Node left;
    public Node right;
    public Node parent;

    public Node(int data) {
        this.data = data;
        color = Color.RED;
        left = null;
        right = null;
        parent = null;
    }

    public Node sibling() {   // if sibling exists
        if (parent == null)
            return null;
        if (isOnLeft())
            return parent.right;
        return parent.left;
    }

    public boolean isOnLeft() {
        return this == parent.left;
    }

    boolean hasRedChild() {
        return (left != null && left.color == Color.RED) || (right != null && right.color == Color.RED);
    }
}

class RedBlackTree {
    public static Node korzen;

    RedBlackTree() {
        korzen = null;
    }

    public static void insert(int value) {
        Node node = new Node(value);
        korzen = BSTInsert(korzen, node);
        korzen = fixAfterBSTInsert(korzen, node);
    }

    private static Node fixAfterBSTInsert(Node root, Node node) {
        Node parentNode = null;
        Node grandParentNode = null;
        while ((node != root) && (node.color != Color.BLACK) && (node.parent.color == Color.RED)) {
            parentNode = node.parent;
            grandParentNode = node.parent.parent;
            if (parentNode == grandParentNode.left) { // if parent is a left child
                Node uncleNode = grandParentNode.right;
                if (uncleNode != null && (uncleNode.color == Color.RED)) { // if uncle is red too
                    grandParentNode.color = Color.RED;
                    parentNode.color = Color.BLACK;
                    uncleNode.color = Color.BLACK;
                    node = grandParentNode;
                }  // if is right child, we must do a left rotation
                else {
                    if (node == parentNode.right) {
                        root = rotateLeft(root, parentNode);
                        node = parentNode;
                        parentNode = node.parent;
                    } // is a left child and do a right- rotation
                    root = rotateRight(root, grandParentNode);
                    swapColors(parentNode, grandParentNode);
                    node = parentNode;
                }
            } else { // parent is a right child
                Node uncleNode = grandParentNode.left;
                if ((uncleNode != null) && (uncleNode.color == Color.RED)) { // red uncle
                    grandParentNode.color = Color.RED;
                    parentNode.color = Color.BLACK;
                    uncleNode.color = Color.BLACK;
                    node = grandParentNode;
                } else {
                    if (node == parentNode.left) {  // left child-> right- rotation
                        root = rotateRight(root, parentNode);
                        node = parentNode;
                        parentNode = node.parent;
                    }
                    root = rotateLeft(root, grandParentNode); // right child-> left rotation

                    swapColors(parentNode, grandParentNode);
                    node = parentNode;
                }
            }
        }
        root.color = Color.BLACK;
        return root;
    }

    private static void swapColors(Node node1, Node node2) {
        Color color = node1.color;
        node1.color = node2.color;
        node2.color = color;
    }

    private static Node rotateLeft(Node root, Node node) {
        Node rightChild = node.right;
        node.right = rightChild.left;
        if (node.right != null)
            node.right.parent = node;
        rightChild.parent = node.parent;
        if (node.parent == null)
            root = rightChild;
        else if (node == node.parent.left)
            node.parent.left = rightChild;
        else
            node.parent.right = rightChild;
        rightChild.left = node;
        node.parent = rightChild;
        return root;
    }

    private static Node rotateRight(Node root, Node node) {
        Node leftChilde = node.left;
        node.left = leftChilde.right;
        if (node.left != null)
            node.left.parent = node;
        leftChilde.parent = node.parent;
        if (node.parent == null)
            root = leftChilde;
        else if (node == node.parent.left)
            node.parent.left = leftChilde;
        else
            node.parent.right = leftChilde;
        leftChilde.right = node;
        node.parent = leftChilde;
        return root;
    }

    private static Node BSTInsert(Node root, Node pt) {
        if (root == null)
            return pt;
        if (pt.data < root.data) {
            root.left = BSTInsert(root.left, pt);
            root.left.parent = root;
        } else if (pt.data > root.data) {
            root.right = BSTInsert(root.right, pt);
            root.right.parent = root;
        }
        return root;
    }

    private static void inOrderHelper(Node root) {
        if (root == null)
            return;
        inOrderHelper(root.left);
        System.out.print(root.data + " ");
        inOrderHelper(root.right);
    }

    public static void inOrder() {
        inOrderHelper(korzen);
        System.out.println();
    }

    public static void swapValues(Node node1, Node node2) {
        int temp = node1.data;
        node1.data = node2.data;
        node2.data = temp;
    }

    public static Node BSTReplace(Node x) {
        if (x.left != null && x.right != null)
            return successor(x.right);
        if (x.left == null && x.right == null)
            return null;
        if (x.left != null)
            return x.left;
        else
            return x.right;
    }

    private static Node deleteNode(Node node) {
        Node temp = BSTReplace(node);
        boolean nodeAndTempBlack = ((temp == null || temp.color == Color.BLACK) && (node.color == Color.BLACK)); // when both black
        Node parent = node.parent;
        if (temp == null) {
            if (node == korzen) {
                korzen = null;
                return null;
            } else {
                if (nodeAndTempBlack)
                    korzen = fixDoubleBlack(node); // when both black, node is a leaf
                else if (node.sibling() != null)
                    node.sibling().color = Color.RED;
            }
            if (node.isOnLeft()) // delete note from the tree
                parent.left = null;
            else
                parent.right = null;
            return korzen;
        }
        if (node.left == null || node.right == null) { // if has one child
            if (node == korzen) {
                node.data = temp.data;
                node.left = node.right = null;
            } else {
                if (node.isOnLeft())
                    parent.left = temp;
                else
                    parent.right = temp;
                temp.parent = parent;
                if (nodeAndTempBlack) { // when both black
                    korzen = fixDoubleBlack(temp);
                } else   // when one is red
                    temp.color = Color.BLACK;
            }
            return korzen;
        }
        swapValues(temp, node);
        korzen = deleteNode(temp);
        return korzen;
    }

    public static Node fixDoubleBlack(Node node) {
        if (node == korzen)
            return korzen;
        Node sibling = node.sibling();
        Node parent = node.parent;
        if (sibling == null)
            korzen = fixDoubleBlack(parent);
        else {
            if (sibling.color == Color.RED) {
                parent.color = Color.RED;
                sibling.color = Color.RED;
                if (sibling.isOnLeft())
                    korzen = rotateRight(korzen, parent);
                else
                    korzen = rotateLeft(korzen, parent);
                korzen = fixDoubleBlack(node);
            } else { // when sibling is black
                if (sibling.hasRedChild()) { // when has a red child
                    if (sibling.left != null && sibling.left.color == Color.RED) {
                        if (sibling.isOnLeft()) {
                            sibling.left.color = sibling.color;
                            sibling.color = parent.color;
                            korzen = rotateRight(korzen, parent);
                        } else {
                            sibling.left.color = parent.color;
                            korzen = rotateRight(korzen, sibling);
                            korzen = rotateLeft(korzen, parent);
                        }
                    } else {
                        if (sibling.isOnLeft()) {
                            sibling.right.color = parent.color;
                            korzen = rotateLeft(korzen, sibling);
                            korzen = rotateLeft(korzen, parent);
                        } else {
                            sibling.right.color = sibling.color;
                            sibling.color = parent.color;
                            korzen = rotateLeft(korzen, parent);
                        }
                    }
                    parent.color = Color.BLACK;
                } else { // both children are black
                    sibling.color = Color.RED;
                    if (parent.color == Color.BLACK)
                        korzen = fixDoubleBlack(parent);
                    else
                        parent.color = Color.BLACK;
                }
            }
        }
        return korzen;
    }

    public static void delete(int x) {
        if (korzen == null)
            return;
        Node v = search(x);
        if (v == null)
            return;
        korzen = deleteNode(v);
    }

    public static Node search(int n) {
        Node temp = korzen;
        while (temp != null) {
            if (n < temp.data) {
                if (temp.left == null)
                    break;
                else
                    temp = temp.left;
            } else if (n == temp.data)
                break;
            else {
                if (temp.right == null)
                    break;
                else
                    temp = temp.right;
            }
        }
        if (temp == null)
            return null;
        if (temp.data != n)
            return null;
        return temp;
    }

    public static Node successor(Node x) {
        Node temp = x;
        while (temp.left != null)
            temp = temp.left;
        return temp;
    }
}

public class RedBlack {
    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();

        //test 1
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(25);
        tree.insert(35);
        tree.inOrder();
        System.out.println(tree.search(20).data);
        System.out.println(tree.search(10).data);
        System.out.println(tree.search(25).data);
        tree.delete(10);
        tree.inOrder();
        tree.delete(25);
        tree.inOrder();
        tree.delete(20);
        tree.inOrder();
        tree.delete(30);
        tree.inOrder();
        tree.delete(35);
        tree.inOrder();

        // test 2
        /*tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(10);
        System.out.println(tree.search(20).data);
        System.out.println(tree.search(40).data);
        tree.inOrder();
        tree.delete(20);
        tree.inOrder();
        tree.delete(30);
        tree.inOrder();
        tree.delete(10);
        tree.inOrder();*/

        // test 3
        /*tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(50);
        tree.inOrder();
        System.out.println(tree.search(20).data);
        tree.delete(20);
        tree.inOrder();
        System.out.println(tree.korzen.data);*/

        // test 4
       /* tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(50);
        tree.insert(35);
        tree.inOrder();
        tree.delete(20);
        tree.inOrder();
        System.out.println(tree.search(35).data);
        tree.delete(35);
        tree.inOrder();*/

        // test 5
        /*tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(25);
        tree.insert(35);
        tree.inOrder();
        tree.delete(10);
        tree.inOrder();
        System.out.println(tree.search(35).data);*/

    }
}

