package net.xcodersteam.eengineer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import net.xcodersteam.eengineer.components.Metal;
import net.xcodersteam.eengineer.components.Silicon;

/**
 * Created by fantasyday on 21.04.2015.
 */
public class MainGameScreen implements Screen{
    @Override
    public void show() {

    }
    ConstructionManager cm;
    SpriteBatch batch;
    ShapeRenderer renderer;
    TextButton buttonP;
    TextButton buttonC;
    TextButton buttonM;
    TextButton viabutton;
    Stage stage;
    ButtonGroup group;
    public void setButton(TextButton button, float x){
        group.add(button);
        button.setWidth(cellSize * 3);
        button.setHeight(cellSize * 3);
        button.setX(Gdx.graphics.getWidth() - cellSize * 3);
        button.setY(Gdx.graphics.getHeight() - cellSize * x);
        stage.addActor(button);
    }
    public  MainGameScreen () {
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        cm = new ConstructionManager(16, 16);
        stage = new Stage();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("bender.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        group = new ButtonGroup();
        buttonP = new TextButton(new String(), style);
        buttonP.setText("Silicon P");
        setButton(buttonP, 3);
        buttonC = new TextButton(new String(), style);
        setButton(buttonC, 6);
        buttonC.setText("Silicon S");
        buttonM = new TextButton(new String(), style);
        setButton(buttonM, 9);
        buttonM.setText("Metal");
        viabutton = new TextButton(new String(), style);
        setButton(viabutton, 12);
        viabutton.setText("Add via");
        group.uncheckAll();
        buttonP.addListener(new ButtonChangeListener(buttonP, "Silicon P", "P - type"));
        buttonC.addListener(new ButtonChangeListener(buttonC, "Silicon S", "S - type"));
        buttonM.addListener(new ButtonChangeListener(buttonM, "Metal", "Set metal"));
        viabutton.addListener(new ButtonChangeListener(viabutton, "Add via", "Via"));
                Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputProcessor() {
                    @Override
                    public boolean keyDown(int keycode) {
                        return false;
                    }

                    @Override
                    public boolean keyUp(int keycode) {
                        return false;
                    }

                    @Override
                    public boolean keyTyped(char character) {
                        return false;
                    }

                    public Cell getCellAt(int screenX, int screenY) {
                        return cm.getCell((screenX - 100) / (cellSize + 1), (Gdx.graphics.getHeight() - screenY - 100) / (cellSize + 1));
                    }
                    public int getCellX(int screenX){
                        return (screenX - 100) / (cellSize + 1);
                    }
                    public int getCellY(int screenY){
                        return (Gdx.graphics.getHeight() - screenY - 100) / (cellSize + 1);
                    }

                    @Override
                    public boolean touchDown(int screenX, int screenY, int pointer, int b) {
                        switch (group.getCheckedIndex()) {
                            case 0:
                                switch (b) {
                                    case Input.Buttons.LEFT:
                                        Cell cell = getCellAt(screenX, screenY);
                                        if (cell != null)
                                            new Silicon(cell, Silicon.Type.P);
                                        break;
                                    case Input.Buttons.RIGHT:
                                        getCellAt(screenX, screenY).layers[1] = null;
                                        if (getCellAt(screenX, screenY).via)
                                            getCellAt(screenX, screenY).via = false;
                                }
                                break;
                            case 1:
                                switch (b) {
                                    case Input.Buttons.LEFT:
                                        Cell cell = getCellAt(screenX, screenY);
                                        if (cell != null)
                                            new Silicon(cell, Silicon.Type.N);
                                        break;
                                    case Input.Buttons.RIGHT:
                                        getCellAt(screenX, screenY).layers[1] = null;
                                        if (getCellAt(screenX, screenY).via)
                                            getCellAt(screenX, screenY).via = false;
                                        break;

                                }
                                break;
                            case 2:
                                switch (b) {
                                    case Input.Buttons.LEFT:
                                        Cell cell = getCellAt(screenX, screenY);
                                        if (cell != null)
                                            new Metal(cell);
                                        break;
                                    case Input.Buttons.RIGHT:
                                        getCellAt(screenX, screenY).layers[2] = null;
                                }
                                break;
                            case 3:
                                switch (b) {
                                    case Input.Buttons.LEFT:
                                        Cell cell = getCellAt(screenX, screenY);
                                        if (cell != null && ((getCellAt(screenX, screenY).layers[1] != null) || (getCellAt(screenX, screenY).layers[2] != null)))
                                            getCellAt(screenX, screenY).via = true;
                                        break;
                                    case Input.Buttons.RIGHT:
                                        getCellAt(screenX, screenY).via = false;
                                }
                                break;
                        }
                        lastScreenX = screenX;
                        lastScreenY = screenY;
                        lastCellX = getCellX(screenX);
                        lastCellY = getCellY(screenY);
                        return true;
                    }

                    @Override
                    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                        return false;
                    }
                    public int lastScreenX;
                    public int lastScreenY;
                    public int lastCellY;
                    public int lastCellX;
                    @Override
                    public boolean touchDragged(int screenX, int screenY, int pointer) {
                        int deltaX = getCellX(screenX) - lastCellX;
                        int deltaY = getCellY(screenY) - lastCellY;
                        if (Math.abs(deltaX) > 0 || Math.abs(deltaY) > 0) {
                            //int deltaX = screenX - lastScreenX;
                            //int deltaY = screenY - lastScreenY;
                            byte dir = 0;
                            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                                if (deltaX > 0)
                                    dir = 2;
                                else
                                    dir = 8;
                            } else {
                                if (deltaY > 0)
                                    dir = 1;
                                else
                                    dir = 4;
                            }
                            lastCellX = getCellX(screenX);
                            lastCellY = getCellY(screenY);
                        }
                        lastScreenX = screenX;
                        lastScreenY = screenY;
                        return true;
                    }

                    @Override
                    public boolean mouseMoved(int screenX, int screenY) {
                        return false;
                    }

                    @Override
                    public boolean scrolled(int amount) {
                        return false;
                    }
                }));
    }

    private final int cellSize = 20;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setAutoShapeType(true);
        renderConstruction(100, 100);
        renderGui();
        renderer.end();
        stage.act(delta);
        stage.draw();
    }
    public void renderGui(){
        renderer.setColor(Color.RED);
        renderer.rect(Gdx.graphics.getWidth() - cellSize * 3, Gdx.graphics.getHeight() - cellSize * 3, cellSize * 3, cellSize * 3);
        renderer.setColor(Color.YELLOW);
        renderer.rect(Gdx.graphics.getWidth() - cellSize * 3, Gdx.graphics.getHeight() - cellSize * 6 - 1, cellSize * 3, cellSize * 3);
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(Gdx.graphics.getWidth() - cellSize * 3, Gdx.graphics.getHeight() - cellSize * 9 - 2, cellSize * 3, cellSize * 3);
        renderer.setColor(Color.NAVY);
        renderer.rect(Gdx.graphics.getWidth() - cellSize * 3, Gdx.graphics.getHeight() - cellSize * 12 - 3, cellSize * 3, cellSize * 3);
    }
    public void renderConstruction(float sx, float sy){
        renderer.translate(sx, sy, 0);
        for(int x = 0; x < cm.width; x++){
            for(int y = 0; y < cm.height; y++){
                renderer.setColor(Color.valueOf("CCCCCC"));
                renderer.rect(x * (cellSize + 1), y * (cellSize + 1), cellSize, cellSize);
                if(cm.construction[x][y] != null) {
                    for (GirdComponent component : cm.construction[x][y].layers) {
                        if (component == null)
                            continue;
                        renderer.setColor(component.getColor());
                        int borderTop = -(component.connection & 1) * 3;
                        int borderRight = -(component.connection >> 1 & 1) * 3;
                        int borderBottom = -(component.connection >> 2 & 1) * 3;
                        int borderLeft = -(component.connection >> 3 & 1) * 3;
                        renderer.rect(x * (cellSize + 1) + borderLeft + 2f, y * (cellSize + 1) + borderBottom + 2f, cellSize - borderLeft - borderRight - 4f, cellSize - borderBottom - borderTop - 4f);
                    }
                    if(cm.construction[x][y].via){
                        renderer.setColor(Color.BLACK);
                        renderer.set(ShapeRenderer.ShapeType.Line);
                        renderer.circle(x * (cellSize + 1) + cellSize / 2, y * (cellSize + 1) + cellSize / 2, 7f);
                        renderer.set(ShapeRenderer.ShapeType.Filled);
                    }
                }
            }
        }
        renderer.translate(-sx, -sy, 0);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
