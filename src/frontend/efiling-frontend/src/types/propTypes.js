import PropTypes from "prop-types";

export const propTypes = {
  header: PropTypes.shape({
    name: PropTypes.string.isRequired,
    history: PropTypes.object.isRequired
  }).isRequired,
  confirmationPopup: PropTypes.shape({
    modal: PropTypes.shape({
      show: PropTypes.bool.isRequired,
      title: PropTypes.string.isRequired,
      body: PropTypes.func.isRequired
    }).isRequired,
    mainButton: PropTypes.shape({
      label: PropTypes.string.isRequired,
      styling: PropTypes.string.isRequired,
      onClick: PropTypes.func.isRequired
    }).isRequired,
    confirmButton: PropTypes.shape({
      label: PropTypes.string.isRequired,
      styling: PropTypes.string.isRequired,
      onClick: PropTypes.func.isRequired
    }).isRequired,
    cancelButton: PropTypes.shape({
      label: PropTypes.string.isRequired,
      styling: PropTypes.string.isRequired,
      onClick: PropTypes.func.isRequired
    }).isRequired
  }).isRequired,
  applicantInfo: PropTypes.shape({
    bceID: PropTypes.string.isRequired,
    firstName: PropTypes.string.isRequired,
    middleName: PropTypes.string,
    lastName: PropTypes.string.isRequired,
    emailAddress: PropTypes.string.isRequired
  }).isRequired
};