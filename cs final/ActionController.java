import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public class ActionController {
    private Stack<LastAction> LastActionStack = new Stack<>();
    private ArrayList<Content> userContent = new ArrayList<>();
    private Stack<Content> lastRemoved = new Stack<>();
    private Stack<Integer> lastRemovedIndex = new Stack<>();
    private Stack<String> lastAdded = new Stack<>();

    public void add(String name, double rating){
        //adds content to userContent and pushes it to LastActionStack for undo method
        userContent.add(new Content(name, rating));
        lastAdded.push(name);
        LastActionStack.push(LastAction.add);
    }
    public void remove(int index){
        if(userContent.isEmpty()){
            return;
        }
        lastRemoved.push(userContent.get(index));
        lastRemovedIndex.push(index);
        userContent.remove(index);
        LastActionStack.push(LastAction.remove);
    }
    public void undo(){
        if(LastActionStack.isEmpty()){
            return;
        }
        LastAction action = LastActionStack.pop();
        //I thought of this while I was trying to sleep just an extra feature I found to be cool.
        switch (action) {
            case add -> {
                for (int i = 0; i < userContent.size(); i++) {
                    if (userContent.get(i).getName().equals(lastAdded.peek())) {
                        userContent.remove(i);
                        lastAdded.pop();
                        break;
                    }
                }
            }
            case remove -> userContent.add(lastRemovedIndex.pop(), lastRemoved.pop());

        }
    }
    public void addAll(ArrayList<Content> contents){
        userContent.addAll(contents);
    }
    public void addContent(Content content){
        userContent.add(content);
    }
    public ArrayList<Content> getContents(){
        userContent.sort(Comparator.comparing(Content::getRating));
        Collections.reverse(userContent);
        return userContent;
    }
}
