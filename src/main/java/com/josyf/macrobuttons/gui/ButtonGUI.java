package com.josyf.macrobuttons.gui;

import com.josyf.macrobuttons.ConfigFile;
import com.josyf.macrobuttons.MacroButtons;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ButtonGUI extends LightweightGuiDescription {

    int xValue = 0;
    int yValue = 1;

    public ButtonGUI() {

        // initialize root panel of GUI
        WGridPanel root = new WGridPanel();
        setupBackground(root);

        // Text GUI, not needed yet
        // WLabel label = new WLabel(new LiteralText("Test"), 0xFFFFFF);
        // root.add(label, 0, 4, 2, 1);

        // Add delete toggle button
        WToggleButton delToggle = new WToggleButton(new TranslatableText("Delete"));
        root.add(delToggle, 17, 0, 3, 1);

        addSavedButtons(root, delToggle);
        addCommandSection(root, delToggle);
        root.validate(this);
    }

    private void addCommandSection(WGridPanel root, WToggleButton toggle) {
        // Add text field for command NAME entry
        WTextField nameTextField = new WTextField();
        nameTextField.setMaxLength(10);
        nameTextField.setSuggestion("Name");
        root.add(nameTextField, 0, 12, 6, 1);

        // Add text field for command / entry
        WTextField commandTextField = new WTextField();
        commandTextField.setSuggestion("/command");
        commandTextField.setMaxLength(100);
        root.add(commandTextField, 6, 12, 11, 1);

        // Add button for command entry
        WButton addCmdBtn = new WButton(new TranslatableText("+"));
        addCmdBtn.setOnClick(() -> {
            addGUIButton(root, nameTextField, commandTextField, toggle);
        });
        root.add(addCmdBtn, 18, 12, 1, 1);
    }

    // Function to save newly added buttons to commands.json
    private void addGUIButton(WGridPanel root, WTextField name, WTextField command, WToggleButton isDeleteToggled) {
        // Only add the button if there are contents in both
        if (!name.getText().equals("") && !command.getText().equals("")) {
            // Create a new Json object & append to masterCommList
            JSONObject newJsonObject = new JSONObject();
            newJsonObject.put("name", name.getText());
            newJsonObject.put("command", command.getText());

            if (!isListTooLong()) {
                String commandString = command.getText();
                WButton button = new WButton(new TranslatableText(name.getText()));
                button.setOnClick(() -> {
                    if (isDeleteToggled.getToggle()) {
                        System.out.println("Should delete " + button.getLabel());
                        ConfigFile.removeObject(newJsonObject);
                        root.remove(button);
                    } else {
                        MacroButtons.runCommand(commandString);
                    }

                });
                root.add(button, xValue, yValue, 4, 1);

                // append the buttons to masterList for future loading
                ConfigFile.addObjectToCommList(newJsonObject);
                ConfigFile.appendToFile(newJsonObject);

                adjustBounds();
            }

            name.setText("");
            command.setText("");

            root.validate(this);

        } else {
            System.out.println("No name and value entered!");
        }

    }

    // function to load buttons from commands.json
    private void addGUIButton(WGridPanel root, String name, String command, WToggleButton isDeleteToggled, JSONObject object) {
        if (!name.equals("") && !command.equals("")) {
            WButton button = new WButton(new TranslatableText(name));
            button.setOnClick(() -> {
                if (isDeleteToggled.getToggle()) {
                    System.out.println("Should delete " + button.getLabel());
                    ConfigFile.removeObject(object);
                    root.remove(button);
                } else {
                    MacroButtons.runCommand(command);
                }

            });
            root.add(button, xValue, yValue, 4, 1);
            adjustBounds();
            root.validate(this);
        } else {
            System.out.println("No name and value entered!");
        }
    }


    // Array will contain String class types. Convert these to objects.
    private void addSavedButtons(WGridPanel root, WToggleButton toggle) {
        ArrayList<JSONObject> commListCopy = MacroButtons.getMasterCommList();
        // Then convert the objects to buttons
        if (commListCopy != null) {
            for (int i = 0; i < commListCopy.size(); i++) {
                JSONObject object = commListCopy.get(i);
                String name = commListCopy.get(i).get("name").toString();
                String command = commListCopy.get(i).get("command").toString();
                addGUIButton(root, name, command, toggle, object);
                if (i >= 19) break;
            }
        }

    }


    private void adjustBounds() {
        if (xValue % 12 == 0 && xValue != 0) {
            yValue += 2;
            xValue = 0;
        } else {
            xValue += 4;
        }
    }

    private boolean isListTooLong() {
        return MacroButtons.getMasterCommList().size() > 19;
    }

    // Change background panel color to transparent black
    @Override
    public void addPainters() {
        super.addPainters();
        this.rootPanel.setBackgroundPainter(BackgroundPainter.createColorful(0x4D000000));
    }

    private void setupBackground(WGridPanel root) {
        setRootPanel(root);
        root.setSize(350, 240);
    }


}
