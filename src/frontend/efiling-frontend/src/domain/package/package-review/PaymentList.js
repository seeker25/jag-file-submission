/* eslint-disable react/jsx-one-expression-per-line, no-nested-ternary */
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import Dinero from "dinero.js";
import { MdPrint } from "react-icons/md";
import "./PaymentList.scss";
import { downloadPaymentReceipt } from "./PaymentListService";
import { errorRedirect } from "../../../modules/helpers/errorRedirect";
import { isEnter } from "../../../modules/helpers/eventUtil";
import { formatCurrency } from "../../../modules/helpers/CurrencyUtil";

const hash = require("object-hash");

export default function PaymentList({ payments, packageId }) {
  const dineroInit = {
    stat: Dinero({ amount: 0 }),
    toDate: Dinero({ amount: 0 }),
  };

  const [subtotal, setSubtotal] = useState(dineroInit);
  const [csoTotal, setCsoTotal] = useState(dineroInit);
  const [total, setTotal] = useState(dineroInit);
  const format = "$0,0.00";

  useEffect(() => {
    let subStat = Dinero({ amount: 0 });
    let subToDate = Dinero({ amount: 0 });
    let csoStat = Dinero({ amount: 0 });
    let csoToDate = Dinero({ amount: 0 });

    if (payments) {
      payments.forEach((payment) => {
        if (payment.paymentCategory === 1 || payment.paymentCategory === 2) {
          csoStat = csoStat.add(
            Dinero({ amount: payment.submittedAmount * 100 })
          );
          csoToDate = csoToDate.add(
            Dinero({ amount: payment.processedAmount * 100 })
          );
        } else if (!payment.feeExempt) {
          subStat = subStat.add(
            Dinero({ amount: payment.submittedAmount * 100 })
          );
          subToDate = subToDate.add(
            Dinero({ amount: payment.processedAmount * 100 || 0 })
          );
        }
      });
    }
    setSubtotal({ stat: subStat, toDate: subToDate });
    setCsoTotal({ stat: csoStat, toDate: csoToDate });
    setTotal({ stat: subStat.add(csoStat), toDate: subToDate.add(csoToDate) });
  }, [payments]);

  const handleClick = () => {
    downloadPaymentReceipt(packageId).catch((err) => {
      errorRedirect(sessionStorage.getItem("errorUrl"), err);
    });
  };

  const handleKeyDown = (e) => {
    if (isEnter(e)) {
      downloadPaymentReceipt(packageId).catch((err) => {
        errorRedirect(sessionStorage.getItem("errorUrl"), err);
      });
    }
  };

  return (
    <div className="ct-payment-list">
      <table className="payment-table table table-borderless">
        <thead>
          <tr>
            <th scope="col">Document</th>
            <th scope="col" className="text-right">
              Statutory Fee
            </th>
            <th scope="col" className="text-right">
              Fees Charged to Date
            </th>
          </tr>
        </thead>
        <tbody>
          {payments &&
            payments
              .filter(
                (payment) =>
                  payment.paymentCategory !== 1 && payment.paymentCategory !== 2
              )
              .map((payment) => (
                <tr key={hash(payment)}>
                  <td>{payment.paymentDescription}</td>
                  <td className=" text-right">
                    {payment.feeExempt
                      ? formatCurrency(0)
                      : formatCurrency(payment.submittedAmount)}
                  </td>
                  <td className="text-right">
                    {payment.feeExempt
                      ? "Exempt"
                      : payment.processedAmount
                      ? formatCurrency(payment.processedAmount)
                      : "Pending"}
                  </td>
                  <td />
                </tr>
              ))}
          {payments && (
            <>
              <tr>
                <td className="text-right">Subtotal: </td>
                <td className="sub-table top-border">
                  {subtotal.stat.toFormat(format)}
                </td>
                <td className="sub-table top-border">
                  {subtotal.toDate.toFormat(format)}
                </td>
                <td />
              </tr>
              <tr>
                <td className="text-right">CSO Fees: </td>
                <td className="sub-table">{csoTotal.stat.toFormat(format)}</td>
                <td className="sub-table">
                  {csoTotal.toDate.toFormat(format)}
                </td>
                <td />
              </tr>
              <tr>
                <td className="text-right">Total Fees: </td>
                <td className="sub-table">{total.stat.toFormat(format)}</td>
                <td className="sub-table">{total.toDate.toFormat(format)}</td>
                <td />
              </tr>
            </>
          )}
        </tbody>
      </table>
      <div className="d-flex justify-content-end">
        <span
          className="file-href"
          role="button"
          data-testid="btn-view-receipt"
          tabIndex={0}
          onClick={handleClick}
          onKeyDown={handleKeyDown}
        >
          View Receipt
        </span>
        <MdPrint size="24" color="#7F7F7F" className="align-icon" />
      </div>
    </div>
  );
}

PaymentList.propTypes = {
  payments: PropTypes.arrayOf(PropTypes.object),
  packageId: PropTypes.number.isRequired,
};

PaymentList.defaultProps = {
  payments: null,
};
