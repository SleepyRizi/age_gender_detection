# age_gender_detection
An android app integrated with tflite model to detected age and gender of person. A CNN was trained on 10,000 images dataset (UTK-faces). Model is then converted into tflite format for integration with android application.
App is structured as; a frame is passed to the face-Cascade file to crop ROI (i.e. human face) from it which then passed on to our model for age and gender prediction. OpenCV 4.5.4 is used as supporting library
