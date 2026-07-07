function _optionalChain(ops) { let lastAccessLHS = undefined; let value = ops[0]; let i = 1; while (i < ops.length) { const op = ops[i]; const fn = ops[i + 1]; i += 2; if ((op === 'optionalAccess' || op === 'optionalCall') && value == null) { return undefined; } if (op === 'access' || op === 'optionalAccess') { lastAccessLHS = value; value = fn(value); } else if (op === 'call' || op === 'optionalCall') { value = fn((...args) => value.call(lastAccessLHS, ...args)); lastAccessLHS = undefined; } } return value; }import React, { useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom';
import { ROUTES } from '@constants/routes';
import { verifyOtp as apiVerifyOtp, resendOtp as apiResendOtp } from '@api/authApi';

export default function OtpActivation() {
    const navigate = useNavigate();
    const { email } = useParams();

    const [codeSent, setCodeSent] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");
    
    const length = 6;
    const [otp, setOtp] = useState(Array(length).fill(""));
    const inputs = useRef([]);

    const handleChange = (value, index) => {
        const newOtp = [...otp];
        newOtp[index] = value.slice(-1);
        setOtp(newOtp);

        if (value && index < length - 1) {
            _optionalChain([inputs, 'access', _ => _.current, 'access', _2 => _2[index + 1], 'optionalAccess', _3 => _3.focus, 'call', _4 => _4()]);
        }
    };

    const handleKeyDown = (e, index) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            _optionalChain([inputs, 'access', _5 => _5.current, 'access', _6 => _6[index - 1], 'optionalAccess', _7 => _7.focus, 'call', _8 => _8()]);
        }
    };
    
    const handleAccountActivationAsync = async (e) => {
        e.preventDefault();
        const code = otp.join("");
        if (code.length !== 6 || isLoading) return;

        setIsLoading(true);
        setErrorMsg("");
        try {
            await apiVerifyOtp({ email, code });
            navigate(ROUTES.SIGN_IN);
        } catch (err) {
            console.error("Failed to activate account", err);
            setErrorMsg(err.message || "An unexpected error occurred. Please try again.");
        } finally {
            setIsLoading(false);
        }
    };
    
    const HandleResendCodeAsync = async () => {
        if (isLoading) return;
        setIsLoading(true);
        setErrorMsg("");
        try {
            await apiResendOtp(email);
            setCodeSent(true);
            setTimeout(() => setCodeSent(false), 4000); // Hide notice after 4s
        } catch (e2) {
            console.error("Failed to resend OTP", e2);
            setErrorMsg(e2.message || "An unexpected error occurred. Please try again.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <article className='w-full min-h-screen flex flex-col items-center bg-bg-base text-text-primary transition-colors duration-300'>

            <div className='flex flex-1 w-full items-center justify-center px-6'>
                <div className='w-full max-w-[450px] flex flex-col items-center'>
                    
                    {/* Header */}
                    <div className='text-center mb-10'>
                        <h2 className='text-4xl font-black tracking-tight text-text-primary font-display'>
                            Verify Access
                        </h2>
                        <p className='text-text-secondary font-medium mt-2'>
                            Sent to <span className="text-text-primary font-bold">{email || "your email"}</span>
                        </p>
                    </div>

                    {/* Verification Card */}
                    <div className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation w-full relative overflow-hidden">
                        {isLoading && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-bg-base overflow-hidden">
                                <div className="h-full bg-accent animate-[loading_1.5s_infinite_linear]" 
                                     style={{ width: '30%', backgroundImage: 'linear-gradient(to right, transparent, var(--color-accent), transparent)' }} 
                                />
                            </div>
                        )}

                        <form onSubmit={handleAccountActivationAsync} className='flex flex-col items-center gap-8'>
                            {errorMsg && (
                                <div role="alert" className="w-full p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-xs font-semibold text-red-500 text-center">
                                    {errorMsg}
                                </div>
                            )}
                            <div className={`flex gap-2 sm:gap-3 transition-opacity duration-300 ${isLoading ? 'opacity-50 pointer-events-none' : ''}`}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={(el) => { inputs.current[index] = el; }}
                                        value={digit}
                                        maxLength={1}
                                        inputMode="numeric"
                                        aria-label={`Digit ${index + 1} of ${length}`}
                                        className="w-10 h-14 sm:w-14 sm:h-16 border border-border-token bg-bg-base rounded-xl text-center text-2xl font-black text-text-primary focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent transition-all"
                                        onChange={(e) => handleChange(e.target.value, index)}
                                        onKeyDown={(e) => handleKeyDown(e, index)}
                                    />
                                ))}
                            </div>

                            <button 
                                type="submit"
                                disabled={isLoading || otp.some(d => !d)}
                                className={`w-full py-4 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base
                                    ${isLoading || otp.some(d => !d)
                                        ? 'bg-bg-base text-text-secondary opacity-50 cursor-not-allowed border border-border-token' 
                                        : 'bg-accent text-white hover:bg-accent-hover active:scale-[0.98] shadow-glow'
                                    }`}
                            >
                                {isLoading ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-text-secondary border-t-text-primary rounded-full animate-spin" />
                                        Verifying...
                                    </>
                                ) : (
                                    "Confirm Identity"
                                )}
                            </button>
                        </form>
                    </div>

                    {/* Footer Actions */}
                    <div className="mt-8 flex flex-col items-center gap-4">
                        <p className='text-sm font-medium text-text-secondary'>
                            Didn't receive the code? 
                            <button 
                                onClick={HandleResendCodeAsync} 
                                className='ml-2 text-accent font-black hover:underline disabled:opacity-50 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded px-1'
                                disabled={isLoading}
                            >
                                RESEND
                            </button>
                        </p>

                        {codeSent && (
                            <div role="status" aria-live="polite" className="px-4 py-2 bg-accent/15 border border-accent/20 rounded-full">
                                <p className='text-accent text-[11px] font-black uppercase tracking-widest'>
                                    New Security Token Sent
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <style>{`
                @keyframes loading {
                    0% { transform: translateX(-100%); }
                    100% { transform: translateX(400%); }
                }
            `}</style>
        </article>
    )
}
