package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application
{
  double scaleVal = 1;
  final double TILE_SIZE = 10;
  final Group root = new Group();
  final Xform world = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final PointLight light = new PointLight(Color.WHITE);
  final Xform cameraXform = new Xform();
  final Xform cameraXform2 = new Xform();
  final Xform cameraXform3 = new Xform();

  final Xform lightXform = new Xform();
  final Xform lightXform2 = new Xform();
  final Xform lightXform3 = new Xform();

  final Group lightGroup = new Group();

  private boolean forward = true;
  private double windowX = 1024;
  private double windowY = 768;
  private static final double WALL_HEIGHT = 16;
  private static final double CAMERA_INITIAL_DISTANCE = -450;
  private static final double CAMERA_INITIAL_X_ANGLE = 90;
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double CONTROL_MULTIPLIER = 0.1;
  private static final double SHIFT_MULTIPLIER = 10.0;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 2.0;
  private static final double TRACK_SPEED = 0.3;

  double mousePosX;
  double mousePosY;
  double mouseOldX;
  double mouseOldY;
  double mouseDeltaX;
  double mouseDeltaY;

  private void buildCamera()
  {
    root.getChildren().add(cameraXform);
    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);// sets y up

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
  }

  private void buildLight()
  {

    light.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    lightXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    lightXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

    light.setTranslateX(CAMERA_INITIAL_X_ANGLE);
    light.setTranslateY(CAMERA_INITIAL_Y_ANGLE);
    lightGroup.getChildren().add(light);
    root.getChildren().add(lightGroup);

  }

  private void drawMap()
  {
    PhongMaterial pathable = new PhongMaterial();
    pathable.setDiffuseColor(Color.DARKGREEN);
    pathable.setSpecularColor(Color.ORANGE);

    PhongMaterial notPathable = new PhongMaterial();
    notPathable.setDiffuseColor(Color.LIGHTGREEN);
    notPathable.setSpecularColor(Color.ORANGE);

    Xform mapXform = new Xform();
    Xform tileXform = new Xform();
    mapXform.getChildren().add(tileXform);
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {

        Box tile = new Box(TILE_SIZE, 1, TILE_SIZE);
        tile.setDrawMode(DrawMode.FILL);
        tile.setTranslateX(i * TILE_SIZE);
        tile.setTranslateZ(j * TILE_SIZE);
        if (i % 2 == 0)
        {
          tile.setTranslateY(0.5);
          tile.setMaterial(pathable);
        }
        else
        {
          tile.setScaleY(WALL_HEIGHT);
          tile.setTranslateY(WALL_HEIGHT / 2);
          tile.setMaterial(notPathable);
        }
        tileXform.getChildren().add(tile);
      }
    }

    world.getChildren().add(mapXform);

  }

  private void handleMouse(Scene scene, final Node root)
  {
    scene.setOnMousePressed(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
      }
    });
    scene.setOnMouseDragged(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 1.0;

        if (me.isControlDown())
        {
          modifier = CONTROL_MULTIPLIER;
        }
        if (me.isShiftDown())
        {
          modifier = SHIFT_MULTIPLIER;
        }
        if (me.isPrimaryButtonDown())
        {
          lightXform.ry.setAngle(lightXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
          lightXform.rx.setAngle(lightXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
          cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
          cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
        }
        else if (me.isSecondaryButtonDown())
        {
          double z = camera.getTranslateZ();
          double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
          camera.setTranslateZ(newZ);
        }
        else if (me.isMiddleButtonDown())
        {
          lightXform2.t.setX(lightXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
          lightXform2.t.setY(lightXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
          System.out.println(cameraXform2.getTranslateY());
        }
      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));

    // array{}{} = clas(120,150)

    buildCamera();
    drawMap();
    buildLight();

    Scene scene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    scene.setFill(lg);
    scene.setCamera(camera);

    handleMouse(scene, world);

    primaryStage.setTitle("Test Application");
    primaryStage.setScene(scene);
    primaryStage.show();

    AnimationTimer gameLoop = new MainGameLoop();
    gameLoop.start();

  }

  class MainGameLoop extends AnimationTimer
  {

    public void handle(long now)
    {

      double z = light.getTranslateZ();
      if (z == 100) forward = false;
      if (z == -100) forward = true;
      //
      if (forward)
      {
        z++;
        // camera.setTranslateZ(z);
        light.setTranslateZ(z);
        System.out.println(z);
      }
      else
      {
        z--;
        light.setTranslateZ(z);
        // camera.setTranslateZ(z);
      }
    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}