package devoworm.portegys_image_transform;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;

/**
 *  Portegys Image Transform:
 *  The mean of the X coordinates of a selected set of image pixels is
 *  used to create a linear vertical target line. Pixel rows are then rectified by
 *  horizontally shifting their selected pixel to the mean.
 *
 *  Adapted from JavaPixelManipulation.java.
 */
public class PortegysImageTransform extends JPanel
{
   private static final long serialVersionUID = 1L;


   /**
    * The main routine simply opens a window that shows a panel.
    */
   public static void main(String[] args)
   {
      @SuppressWarnings("unused")
      PortegysImageTransform transform = new PortegysImageTransform();
   }


   private JFrame        window;
   private BufferedImage OSC;                 // Stores a copy of the panel content.
   private Graphics2D    OSG;                 // Graphics context for drawing to OSC/
   private Color         color = Color.BLACK; // The current drawing color.
   private BasicStroke   stroke;              // The current stroke, used for lines and curves.

   private BufferedImage currentImage = null; // Current loaded image,
   // for convenience.  A "Reload Image" menu
   // item will load the same image.
   private JMenuItem saveImageMenuItem;    // The "Save Image" menu item.
   private JMenuItem reloadImageMenuItem;  // The "Reload Image" menu item.

   private int   imageWidth  = -1;
   private int   imageHeight = -1;
   private int[] xMarks      = null; // X coordinate marks.


   /**
    * The constructor sets the preferred size of the panel, creates the BufferedImage,
    * and installs a mouse listener on the panel to implement drawing actions.
    */
   public PortegysImageTransform()
   {
      setPreferredSize(new Dimension(640, 480));
      OSC = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
      OSG = OSC.createGraphics();
      OSG.setColor(Color.WHITE);
      OSG.fillRect(0, 0, 640, 480);
      OSG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
      addMouseListener(new MouseHandler());
      window = new JFrame("Portegys Image Transform");
      window.setContentPane(this);
      window.setJMenuBar(this.getMenuBar());
      window.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation((screenSize.width - window.getWidth()) / 2,
                         (screenSize.height - window.getHeight()) / 2);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setVisible(true);
   }


   /**
    * Resize content.
    */
   private void resizeContent(int width, int height)
   {
      setPreferredSize(new Dimension(width, height));
      OSC = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      OSG = OSC.createGraphics();
      window.pack();
   }


   /**
    * The paintComponent() copies the BufferedImage to the screen.
    */
   protected void paintComponent(Graphics g)
   {
      g.drawImage(OSC, 0, 0, null);
   }


   /**
    *  Draw a point at (x, y).
    *  If the repaint parameter is true, then the panel's repaint() method is called for a rectangle
    *  that contains the point.  When this method is used to draw to the BufferedImage, repaint is
    *  true, so that the change to the BufferedImage will also be shown on the screen.
    */
   private void putPoint(Graphics2D g, int x, int y, boolean repaint)
   {
      g.setColor(color);
      g.setStroke(stroke);
      g.drawLine(x, y, x, y);
      if (repaint)
      {
         repaint(x - 5, y - 5, 10, 10); // large enough to contain widest stroke
      }
   }


   /**
    *  Defines the mouse listener object that responds to user mouse actions on
    *  the panel.
    */
   private class MouseHandler extends MouseAdapter {
      boolean dragging = false; // Set to true if a dragging operation is in progress.
      int     startX, startY;   // The point at which the drag action started.

      public void mousePressed(MouseEvent evt)
      {
         if (dragging)
         {
            return;      // There is already a mouse drag in progress; don't try to start a new one.
         }
         dragging = true;
         startX   = evt.getX();
         startY   = evt.getY();
         putPoint(OSG, startX, startY, true);
         if (xMarks != null)
         {
            xMarks[startY] = startX;
         }
         addMouseMotionListener(this);
      }


      public void mouseDragged(MouseEvent evt)
      {
         if (!dragging)
         {
            return;
         }
         int x = evt.getX();
         int y = evt.getY();
         putPoint(OSG, x, y, true);
         if (xMarks != null)
         {
            xMarks[y] = x;
         }
      }


      public void mouseReleased(MouseEvent evt)
      {
         if (!dragging)
         {
            return;
         }
         removeMouseMotionListener(this);
         dragging = false;
      }
   }

   /**
    * Load an image from a file selected by the user.  The image is scaled to
    * exactly fill the panel, possibly changing the aspect ratio.
    */
   private void loadImageFile()
   {
      JFileChooser fileDialog;

      fileDialog = new JFileChooser();
      fileDialog.setSelectedFile(null);
      int option = fileDialog.showOpenDialog(this);
      if (option != JFileChooser.APPROVE_OPTION)
      {
         return;     // User canceled or clicked the dialog's close box.
      }
      File            selectedFile = fileDialog.getSelectedFile();
      FileInputStream stream;
      try {
         stream = new FileInputStream(selectedFile);
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
                                       "Sorry, but an error occurred while trying to open the file:\n" + e);
         return;
      }
      try {
         BufferedImage image = ImageIO.read(stream);
         if (image == null)
         {
            throw new Exception("File does not contain a recognized image format.");
         }
         resizeContent(image.getWidth(), image.getHeight());
         Graphics g = OSC.createGraphics();
         g.drawImage(image, 0, 0, OSC.getWidth(), OSC.getHeight(), null);
         g.dispose();
         repaint();
         currentImage = image;
         imageWidth   = image.getWidth();
         imageHeight  = image.getHeight();
         reloadImageMenuItem.setEnabled(true); // Enable the "Reload Image command.
         saveImageMenuItem.setEnabled(true);   // Enable the "Save Image command.
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
                                       "Sorry, but an error occurred while trying to read the image:\n" + e);
      }
   }


   /**
    * Save an image to a file given by the user.
    */
   private void saveImageFile()
   {
      JFileChooser fileDialog;

      fileDialog = new JFileChooser();
      fileDialog.setSelectedFile(null);
      int option = fileDialog.showOpenDialog(this);
      if (option != JFileChooser.APPROVE_OPTION)
      {
         return;             // User canceled or clicked the dialog's close box.
      }
      File outputFile = fileDialog.getSelectedFile();
      try {
         ImageIO.write(currentImage, "png", outputFile);
      }
      catch (IOException e) {
         JOptionPane.showMessageDialog(this,
                                       "Sorry, but an error occurred while trying to write the image:\n" + e);
      }
   }


   /**
    * Transform the image.
    */
   private void transformImage()
   {
      if (imageHeight == -1)
      {
         JOptionPane.showMessageDialog(this, "Error: image not loaded");
         return;
      }

      // Target X is mean of marked pixels.
      int xTarget = 0;
      int n       = 0;
      for (int i = 0; i < imageHeight; i++)
      {
         // Interpolate mark?
         if (xMarks[i] == -1)
         {
            int x1 = -1;
            int x2 = -1;
            for (int j = i - 1; j >= 0; j--)
            {
               if (xMarks[j] != -1)
               {
                  x1 = j;
                  break;
               }
            }
            if (x1 != -1)
            {
               for (int j = i + 1; j < imageHeight; j++)
               {
                  if (xMarks[j] != -1)
                  {
                     x2 = j;
                     break;
                  }
               }
            }
            if ((x1 != -1) && (x2 != -1))
            {
               xMarks[i] = (xMarks[x1] + xMarks[x2]) / 2;
            }
         }
         if (xMarks[i] != -1)
         {
            xTarget += xMarks[i];
            n++;
         }
      }
      if (n == 0)
      {
         JOptionPane.showMessageDialog(this, "Error: no selected pixels");
         return;
      }
      xTarget = (int)((float)xTarget / (float)n);

      // Determine shift extremes.
      int leftShift  = 0;
      int rightShift = 0;
      for (int i = 0; i < imageHeight; i++)
      {
         if (xMarks[i] != -1)
         {
            if (xMarks[i] > xTarget)
            {
               if ((xMarks[i] - xTarget) > leftShift)
               {
                  leftShift = (xMarks[i] - xTarget);
               }
            }
            else if (xMarks[i] < xTarget)
            {
               if ((xTarget - xMarks[i]) > rightShift)
               {
                  rightShift = (xTarget - xMarks[i]);
               }
            }
         }
      }

      // Create image for transform with additional left and right margins for shifting.
      BufferedImage transformImage = new BufferedImage(imageWidth + leftShift + rightShift,
                                                       imageHeight, BufferedImage.TYPE_INT_RGB);
      transformImage.createGraphics();
      int        w = transformImage.getWidth();
      Graphics2D g = (Graphics2D)transformImage.getGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, w, imageHeight);
      g.drawImage(currentImage, leftShift, 0, leftShift + imageWidth, imageHeight,
                  0, 0, imageWidth, imageHeight, Color.WHITE, null);
      currentImage = transformImage;
      imageWidth   = w;

      // Transform.
      for (int i = 0; i < imageHeight; i++)
      {
         if (xMarks[i] != -1)
         {
            if (xMarks[i] > xTarget)
            {
               int shift = (xMarks[i] - xTarget);
               for (int j = 0; j < imageWidth; j++)
               {
                  if ((j + shift) < imageWidth)
                  {
                     currentImage.setRGB(j, i, currentImage.getRGB(j + shift, i));
                  }
                  else
                  {
                     currentImage.setRGB(j, i, Color.WHITE.getRGB());
                  }
               }
            }
            else if (xMarks[i] < xTarget)
            {
               int shift = (xTarget - xMarks[i]);
               for (int j = imageWidth - 1; j >= 0; j--)
               {
                  if ((j - shift) >= 0)
                  {
                     currentImage.setRGB(j, i, currentImage.getRGB(j - shift, i));
                  }
                  else
                  {
                     currentImage.setRGB(j, i, Color.WHITE.getRGB());
                  }
               }
            }
            xMarks[i] = xTarget + leftShift;
         }
      }


      // Display.
      resizeContent(imageWidth, imageHeight);
      OSG.drawImage(currentImage, 0, 0, OSC.getWidth(), OSC.getHeight(), null);
      for (int i = 0; i < imageHeight; i++)
      {
         if (xMarks[i] != -1)
         {
            putPoint(OSG, xMarks[i], i, false);
         }
      }
      repaint();
   }


   /**
    * Create the menus for the program, and provide listeners to implement the menu commands.
    */
   private JMenuBar getMenuBar()
   {
      JMenuBar       menuBar   = new JMenuBar();
      ActionListener flistener = new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            switch (evt.getActionCommand())
            {
            case "Clear":
               OSG.setColor(Color.WHITE);
               OSG.fillRect(0, 0, OSC.getWidth(), OSC.getHeight());
               repaint();
               if (imageHeight != -1)
               {
                  xMarks = new int[imageHeight];
                  for (int i = 0; i < imageHeight; i++) { xMarks[i] = -1; }
               }
               break;

            case "Quit":
               System.exit(0);
               break;

            case "Load Image...":
               loadImageFile();
               if (imageHeight != -1)
               {
                  xMarks = new int[imageHeight];
                  for (int i = 0; i < imageHeight; i++) { xMarks[i] = -1; }
               }
               break;

            case "Reload Image":
               OSG.drawImage(currentImage, 0, 0, OSC.getWidth(), OSC.getHeight(), null);
               repaint();
               if (imageHeight != -1)
               {
                  xMarks = new int[imageHeight];
                  for (int i = 0; i < imageHeight; i++) { xMarks[i] = -1; }
               }
               break;

            case "Save Image":
               saveImageFile();
               break;
            }
         }
      };
      JMenu file = new JMenu("File");

      file.add(makeMenuItem("Clear", flistener));
      file.add(makeMenuItem("Load Image...", flistener));
      reloadImageMenuItem = makeMenuItem("Reload Image", flistener);
      file.add(reloadImageMenuItem);
      reloadImageMenuItem.setEnabled(false);    // Command will be enabled when an image is loaded.
      saveImageMenuItem = makeMenuItem("Save Image", flistener);
      file.add(saveImageMenuItem);
      saveImageMenuItem.setEnabled(false);    // Command will be enabled when an image is loaded.
      file.addSeparator();
      file.add(makeMenuItem("Quit", flistener));
      menuBar.add(file);
      ActionListener tlistener = new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            String action = evt.getActionCommand();

            if ((action != null) && action.equals("Run"))
            {
               transformImage();
            }
         }
      };
      JMenu transform = new JMenu("Transform");
      transform.add(makeMenuItem("Run", tlistener));
      menuBar.add(transform);
      ActionListener clistener = new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            switch (evt.getActionCommand())
            {
            case "Black":
               color = Color.BLACK;
               return;

            case "Red":
               color = Color.RED;
               return;

            case "Green":
               color = Color.GREEN;
               return;

            case "Blue":
               color = Color.BLUE;
               return;

            case "Cyan":
               color = Color.CYAN;
               return;

            case "Magenta":
               color = Color.MAGENTA;
               return;

            case "Yellow":
               color = Color.YELLOW;
               return;

            case "Gray":
               color = Color.GRAY;
               return;

            case "Custom...":
               Color c = JColorChooser.showDialog(
                  PortegysImageTransform.this, "Select Drawing Color", color);
               if (c != null)
               {
                  color = c;
               }
            }
         }
      };
      JMenu colors = new JMenu("Color");
      colors.add(makeMenuItem("Black", clistener));
      colors.add(makeMenuItem("Red", clistener));
      colors.add(makeMenuItem("Green", clistener));
      colors.add(makeMenuItem("Blue", clistener));
      colors.add(makeMenuItem("Cyan", clistener));
      colors.add(makeMenuItem("Yellow", clistener));
      colors.add(makeMenuItem("Magenta", clistener));
      colors.add(makeMenuItem("Gray", clistener));
      colors.add(makeMenuItem("Custom...", clistener));
      menuBar.add(colors);
      stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
      return(menuBar);
   }


   /**
    * Utility method used by getMenuBar to create menu items.
    */
   private JMenuItem makeMenuItem(String itemName, ActionListener listener)
   {
      JMenuItem item = new JMenuItem(itemName);

      item.addActionListener(listener);
      return(item);
   }
}
