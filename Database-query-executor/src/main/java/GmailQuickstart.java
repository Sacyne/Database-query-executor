public class GmailQuickstart {


//    public static void main(String... args) throws IOException, GeneralSecurityException {
//        // Build a new authorized API client service.
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        // Print the labels in the user's account.
//        String user = "me";
////        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
////        List<Label> labels = listResponse.getLabels();
////        if (labels.isEmpty()) {
////            System.out.println("No labels found.");
////        } else {
////            System.out.println("Labels:");
////            for (Label label : labels) {
////                System.out.printf("- %s\n", label.getName());
////            }
////        }
//        try {
//            Mail.sendMessage(service,user,Mail.createEmail("swordsman94@gmail.com","alr.leroy@gmail.com","TEST MAIL JAVA","Email sent from java"));
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//    }
}